import 'dotenv/config';
import express from 'express';
import cors from 'cors';
import http from 'http';
import { Server } from 'socket.io';
import { env } from './env.js';
import { registerRouter } from './routes/auth.js';
import { profileRouter } from './routes/profile.js';
import { swipeRouter } from './routes/swipe.js';
import { chatRouter } from './routes/chat.js';

const app = express();
app.use(cors({ origin: env.FRONTEND_ORIGIN, credentials: true }));
app.use(express.json());

app.get('/health', (_req, res) => {
  res.json({ ok: true });
});

app.use('/auth', registerRouter);
app.use('/profile', profileRouter);
app.use('/swipe', swipeRouter);
app.use('/chat', chatRouter);

const server = http.createServer(app);
const io = new Server(server, {
  cors: { origin: env.FRONTEND_ORIGIN }
});

io.on('connection', (socket) => {
  socket.on('join_match', (matchId: string) => {
    socket.join(`match:${matchId}`);
  });
  socket.on('message', ({ matchId, message }: { matchId: string; message: any }) => {
    io.to(`match:${matchId}`).emit('message', message);
  });
});

server.listen(env.PORT, () => {
  console.log(`API listening on http://localhost:${env.PORT}`);
});