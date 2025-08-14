import 'dotenv/config';
import express, { Request, Response, NextFunction } from 'express';
import http from 'http';
import cors from 'cors';
import helmet from 'helmet';
import cookieParser from 'cookie-parser';
import { Server as SocketIOServer } from 'socket.io';
import { PrismaClient, User as PrismaUser } from '@prisma/client';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';

const prisma = new PrismaClient();

const app = express();
const server = http.createServer(app);
const io = new SocketIOServer(server, {
  cors: { origin: '*', credentials: false }
});

app.use(helmet());
app.use(cors({ origin: '*', credentials: false }));
app.use(express.json());
app.use(cookieParser());

const JWT_SECRET = process.env.JWT_SECRET || 'dev-secret-change-me';

interface JwtPayload {
  userId: string;
}

function signToken(userId: string): string {
  return jwt.sign({ userId } as JwtPayload, JWT_SECRET, { expiresIn: '15d' });
}

function authMiddleware(req: Request & { userId?: string }, res: Response, next: NextFunction) {
  const authHeader = req.headers['authorization'] || '';
  const token = authHeader.startsWith('Bearer ') ? authHeader.substring(7) : undefined;
  if (!token) return res.status(401).json({ error: 'Unauthenticated' });
  try {
    const decoded = jwt.verify(token, JWT_SECRET) as JwtPayload;
    req.userId = decoded.userId;
    next();
  } catch (err) {
    return res.status(401).json({ error: 'Invalid token' });
  }
}

function parseInterests(interests: string | null): string[] {
  if (!interests) return [];
  try {
    const parsed = JSON.parse(interests);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function serializeInterests(interests: unknown): string | undefined {
  if (Array.isArray(interests)) return JSON.stringify(interests);
  if (typeof interests === 'string') return interests; // allow raw string
  if (interests == null) return undefined;
  return JSON.stringify([]);
}

function sanitizeUser(u: PrismaUser) {
  const { passwordHash, interests, ...rest } = u as any;
  return { ...rest, interests: parseInterests((u as any).interests ?? null) };
}

// Health check
app.get('/health', (_req, res) => {
  res.json({ status: 'ok' });
});

// Auth
app.post('/auth/register', async (req, res) => {
  const { email, password, name } = req.body as { email?: string; password?: string; name?: string };
  if (!email || !password || !name) return res.status(400).json({ error: 'email, password, name required' });
  const existing = await prisma.user.findUnique({ where: { email } });
  if (existing) return res.status(409).json({ error: 'Email already in use' });
  const passwordHash = await bcrypt.hash(password, 10);
  const user = await prisma.user.create({ data: { email, passwordHash, name, interests: JSON.stringify([]) } });
  const token = signToken(user.id);
  return res.json({ token, user: sanitizeUser(user) });
});

app.post('/auth/login', async (req, res) => {
  const { email, password } = req.body as { email?: string; password?: string };
  if (!email || !password) return res.status(400).json({ error: 'email, password required' });
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) return res.status(401).json({ error: 'Invalid credentials' });
  const ok = await bcrypt.compare(password, user.passwordHash);
  if (!ok) return res.status(401).json({ error: 'Invalid credentials' });
  const token = signToken(user.id);
  return res.json({ token, user: sanitizeUser(user) });
});

// Profile
app.get('/profiles/me', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const me = await prisma.user.findUnique({ where: { id: req.userId! } });
  return res.json({ user: me ? sanitizeUser(me) : null });
});

app.put('/profiles/me', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const { name, photoUrl, bio, interests, sector } = req.body as Partial<{ name: string; photoUrl: string; bio: string; interests: string[] | string; sector: string }>
  const updated = await prisma.user.update({
    where: { id: req.userId! },
    data: { name, photoUrl, bio, interests: serializeInterests(interests), sector }
  });
  return res.json({ user: sanitizeUser(updated) });
});

// Browse profiles
app.get('/profiles/browse', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const sector = typeof req.query.sector === 'string' ? req.query.sector : undefined;
  const limit = Math.min(Number(req.query.limit ?? 20), 50);

  const swipedIds = await prisma.swipe.findMany({
    where: { fromUserId: req.userId! },
    select: { toUserId: true }
  }).then(list => list.map(x => x.toUserId));

  const users = await prisma.user.findMany({
    where: {
      id: { not: req.userId!, notIn: swipedIds },
      ...(sector ? { sector } : {})
    },
    take: limit
  });

  return res.json({ users: users.map(sanitizeUser) });
});

// Swipes
app.post('/swipes', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const { toUserId, liked } = req.body as { toUserId?: string; liked?: boolean };
  if (!toUserId || typeof liked !== 'boolean') return res.status(400).json({ error: 'toUserId, liked required' });
  if (toUserId === req.userId) return res.status(400).json({ error: 'Cannot swipe yourself' });

  const swipe = await prisma.swipe.upsert({
    where: { fromUserId_toUserId: { fromUserId: req.userId!, toUserId } },
    create: { fromUserId: req.userId!, toUserId, liked },
    update: { liked }
  });

  let matchCreated: any = null;
  if (liked) {
    const reciprocal = await prisma.swipe.findUnique({
      where: { fromUserId_toUserId: { fromUserId: toUserId, toUserId: req.userId! } }
    });
    if (reciprocal?.liked) {
      const [a, b] = [req.userId!, toUserId].sort();
      const existingMatch = await prisma.match.findFirst({ where: { userAId: a, userBId: b } });
      if (!existingMatch) {
        matchCreated = await prisma.match.create({ data: { userAId: a, userBId: b } });
      } else {
        matchCreated = existingMatch;
      }
    }
  }
  return res.json({ swipe, match: matchCreated });
});

// Matches list
app.get('/matches', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const myId = req.userId!;
  const matches = await prisma.match.findMany({
    where: { OR: [{ userAId: myId }, { userBId: myId }] },
    include: {
      messages: { orderBy: { createdAt: 'desc' }, take: 1 },
      userA: true,
      userB: true
    },
    orderBy: { createdAt: 'desc' }
  });

  const data = matches.map(m => {
    const other = m.userAId === myId ? m.userB : m.userA;
    const last = m.messages[0];
    return {
      id: m.id,
      createdAt: m.createdAt,
      otherUser: sanitizeUser(other as any),
      lastMessage: last ? { text: last.text, createdAt: last.createdAt } : null
    };
  });

  return res.json({ matches: data });
});

// Messages REST
app.get('/matches/:matchId/messages', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const matchId = req.params.matchId;
  const match = await prisma.match.findUnique({ where: { id: matchId } });
  if (!match) return res.status(404).json({ error: 'Match not found' });
  if (match.userAId !== req.userId && match.userBId !== req.userId) return res.status(403).json({ error: 'Forbidden' });
  const messages = await prisma.message.findMany({ where: { matchId }, orderBy: { createdAt: 'asc' } });
  return res.json({ messages });
});

app.post('/matches/:matchId/messages', authMiddleware, async (req: Request & { userId?: string }, res: Response) => {
  const matchId = req.params.matchId;
  const { text } = req.body as { text?: string };
  if (!text) return res.status(400).json({ error: 'text required' });
  const match = await prisma.match.findUnique({ where: { id: matchId } });
  if (!match) return res.status(404).json({ error: 'Match not found' });
  if (match.userAId !== req.userId && match.userBId !== req.userId) return res.status(403).json({ error: 'Forbidden' });
  const message = await prisma.message.create({ data: { matchId, senderId: req.userId!, text } });
  io.to(`match:${matchId}`).emit('message:new', { message });
  return res.json({ message });
});

// Socket.IO auth and events
io.use((socket, next) => {
  const authHeader = (socket.handshake.auth?.token as string) || '';
  const token = authHeader.startsWith('Bearer ') ? authHeader.substring(7) : authHeader;
  if (!token) return next(new Error('Unauthenticated'));
  try {
    const decoded = jwt.verify(token, JWT_SECRET) as JwtPayload;
    (socket as any).userId = decoded.userId;
    next();
  } catch (err) {
    next(new Error('Invalid token'));
  }
});

io.on('connection', async (socket) => {
  const userId: string = (socket as any).userId;
  const matches = await prisma.match.findMany({ where: { OR: [{ userAId: userId }, { userBId: userId }] } });
  for (const m of matches) {
    socket.join(`match:${m.id}`);
  }

  socket.on('message:send', async (payload: { matchId: string; text: string }) => {
    try {
      const match = await prisma.match.findUnique({ where: { id: payload.matchId } });
      if (!match) return;
      if (match.userAId !== userId && match.userBId !== userId) return;
      const message = await prisma.message.create({ data: { matchId: payload.matchId, senderId: userId, text: payload.text } });
      io.to(`match:${payload.matchId}`).emit('message:new', { message });
    } catch (e) {
      // noop
    }
  });
});

const PORT = Number(process.env.PORT || 4000);
server.listen(PORT, () => {
  // eslint-disable-next-line no-console
  console.log(`API listening on http://localhost:${PORT}`);
});