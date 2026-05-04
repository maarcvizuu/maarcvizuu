import { Router } from 'express';
import { prisma } from '../prisma.js';
import { authMiddleware } from './auth.js';

export const swipeRouter = Router();

// Obtener candidatos simples (excluye a uno mismo y a quienes ya se les dio like/no-like en MVP solo excluimos likes dados)
swipeRouter.get('/candidates', authMiddleware, async (req: any, res) => {
  const meId = req.userId as string;
  const likes = await prisma.like.findMany({ where: { fromId: meId }, select: { toId: true } });
  const likedIds = new Set(likes.map((l) => l.toId));

  const candidates = await prisma.user.findMany({
    where: { id: { not: meId } },
    select: { id: true, name: true, bio: true, photoUrl: true, sector: true, interests: true },
    take: 50
  });

  const filtered = candidates.filter((c) => !likedIds.has(c.id));
  res.json(filtered);
});

// Like
swipeRouter.post('/like/:toId', authMiddleware, async (req: any, res) => {
  const fromId = req.userId as string;
  const toId = req.params.toId as string;
  if (fromId === toId) return res.status(400).json({ error: 'Cannot like yourself' });

  try {
    await prisma.like.create({ data: { fromId, toId } });
  } catch (e) {
    // unique constraint -> ignore
  }

  const reciprocal = await prisma.like.findUnique({ where: { fromId_toId: { fromId: toId, toId: fromId } } });
  if (reciprocal) {
    const [a, b] = fromId < toId ? [fromId, toId] : [toId, fromId];
    const match = await prisma.match.upsert({
      where: { userAId_userBId: { userAId: a, userBId: b } },
      update: {},
      create: { userAId: a, userBId: b }
    });
    return res.json({ liked: true, match: { id: match.id, userAId: match.userAId, userBId: match.userBId } });
  }

  res.json({ liked: true });
});