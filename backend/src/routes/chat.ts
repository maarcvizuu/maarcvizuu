import { Router } from 'express';
import { prisma } from '../prisma.js';
import { authMiddleware } from './auth.js';
import { z } from 'zod';

export const chatRouter = Router();

// Lista de matches del usuario autenticado
chatRouter.get('/matches', authMiddleware, async (req: any, res) => {
  const userId = req.userId as string;
  const matches = await prisma.match.findMany({
    where: { OR: [{ userAId: userId }, { userBId: userId }] },
    include: {
      userA: { select: { id: true, name: true, photoUrl: true } },
      userB: { select: { id: true, name: true, photoUrl: true } }
    },
    orderBy: { createdAt: 'desc' }
  });

  const result = matches.map((m) => ({
    id: m.id,
    other: m.userAId === userId ? m.userB : m.userA
  }));
  res.json(result);
});

// Obtener mensajes de un match
chatRouter.get('/messages/:matchId', authMiddleware, async (req: any, res) => {
  const userId = req.userId as string;
  const matchId = req.params.matchId as string;
  const match = await prisma.match.findUnique({ where: { id: matchId } });
  if (!match || (match.userAId !== userId && match.userBId !== userId)) return res.status(404).json({ error: 'Not found' });
  const messages = await prisma.message.findMany({ where: { matchId }, orderBy: { createdAt: 'asc' } });
  res.json(messages);
});

// Enviar mensaje
const messageSchema = z.object({ content: z.string().min(1).max(1000) });
chatRouter.post('/messages/:matchId', authMiddleware, async (req: any, res) => {
  const userId = req.userId as string;
  const matchId = req.params.matchId as string;
  const parsed = messageSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  const match = await prisma.match.findUnique({ where: { id: matchId } });
  if (!match || (match.userAId !== userId && match.userBId !== userId)) return res.status(404).json({ error: 'Not found' });

  const msg = await prisma.message.create({ data: { matchId, senderId: userId, content: parsed.data.content } });
  res.json(msg);
});