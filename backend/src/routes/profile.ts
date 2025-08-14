import { Router } from 'express';
import { prisma } from '../prisma.js';
import { authMiddleware } from './auth.js';
import { z } from 'zod';

export const profileRouter = Router();

profileRouter.get('/me', authMiddleware, async (req: any, res) => {
  const me = await prisma.user.findUnique({
    where: { id: req.userId },
    select: { id: true, email: true, name: true, photoUrl: true, bio: true, sector: true, interests: true }
  });
  res.json(me);
});

const updateSchema = z.object({
  name: z.string().min(1).optional(),
  photoUrl: z.string().url().nullable().optional(),
  bio: z.string().max(500).nullable().optional(),
  sector: z.string().nullable().optional(),
  interests: z.array(z.string()).optional()
});

profileRouter.put('/me', authMiddleware, async (req: any, res) => {
  const parsed = updateSchema.safeParse(req.body);
  if (!parsed.success) return res.status(400).json({ error: parsed.error.flatten() });

  const data = parsed.data as any;
  const updated = await prisma.user.update({ where: { id: req.userId }, data });
  res.json({ ok: true, user: { id: updated.id, name: updated.name, photoUrl: updated.photoUrl, bio: updated.bio, sector: updated.sector, interests: updated.interests } });
});