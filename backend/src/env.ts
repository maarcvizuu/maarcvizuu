import { z } from 'zod';

const envSchema = z.object({
  DATABASE_URL: z.string().url(),
  JWT_SECRET: z.string().min(8),
  PORT: z.string().transform((v) => parseInt(v, 10)).default('4000'),
  FRONTEND_ORIGIN: z.string().url()
});

export const env = envSchema.parse({
  DATABASE_URL: process.env.DATABASE_URL,
  JWT_SECRET: process.env.JWT_SECRET,
  PORT: process.env.PORT ?? '4000',
  FRONTEND_ORIGIN: process.env.FRONTEND_ORIGIN
});