# Startup Match Monorepo (MVP)

- Frontend: Next.js (TypeScript, Tailwind) PWA-ready
- Backend: Express + Prisma + Socket.IO
- DB: PostgreSQL (Docker Compose)

## Requisitos
- Node.js >= 18
- Docker + Docker Compose (opcional pero recomendado para DB)

## Primer uso
1. Levanta PostgreSQL:
```bash
docker compose up -d
```
2. Instala dependencias:
```bash
npm install
```
3. Inicializa base de datos (Prisma):
```bash
npm run db:push
```
4. Ejecuta desarrollo full-stack:
```bash
npm run dev
```

Variables de entorno principales:
- Backend (`/backend/.env`):
  - `DATABASE_URL=postgresql://app:app@localhost:5432/app?schema=public`
  - `JWT_SECRET=supersecret_dev_change_me`
  - `PORT=4000`
  - `FRONTEND_ORIGIN=http://localhost:3000`
- Frontend (`/frontend/.env.local`):
  - `NEXT_PUBLIC_API_URL=http://localhost:4000`
