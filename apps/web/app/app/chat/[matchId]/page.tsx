"use client"
import { useEffect, useMemo, useRef, useState } from 'react'
import { useParams } from 'next/navigation'
import { io } from 'socket.io-client'
import { api } from '@/lib/api'

export default function ChatPage() {
  const params = useParams<{ matchId: string }>()
  const matchId = params.matchId
  const [messages, setMessages] = useState<any[]>([])
  const [text, setText] = useState('')
  const bottomRef = useRef<HTMLDivElement | null>(null)

  const socket = useMemo(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null
    return io(process.env.NEXT_PUBLIC_API_URL || 'http://localhost:4000', {
      autoConnect: false,
      auth: { token: token ? `Bearer ${token}` : '' }
    })
  }, [])

  useEffect(() => {
    api.get(`/matches/${matchId}/messages`).then(res => setMessages(res.data.messages))
  }, [matchId])

  useEffect(() => {
    socket.connect()
    socket.on('message:new', (payload: any) => {
      if (payload?.message?.matchId === matchId) {
        setMessages(prev => [...prev, payload.message])
      }
    })
    return () => {
      socket.disconnect()
    }
  }, [socket, matchId])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages.length])

  async function sendMessage(e: React.FormEvent) {
    e.preventDefault()
    if (!text.trim()) return
    const res = await api.post(`/matches/${matchId}/messages`, { text })
    setMessages(prev => [...prev, res.data.message])
    setText('')
  }

  return (
    <main className="flex flex-col h-[calc(100vh-120px)]">
      <div className="flex-1 overflow-y-auto space-y-2">
        {messages.map(m => (
          <div key={m.id} className="px-3 py-1">
            <div className="inline-block rounded-lg bg-gray-100 px-3 py-2 text-sm">{m.text}</div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>
      <form onSubmit={sendMessage} className="mt-2 flex gap-2">
        <input value={text} onChange={e=>setText(e.target.value)} className="flex-1 border rounded px-3 py-2" placeholder="Escribe un mensaje" />
        <button className="rounded bg-black text-white px-4 py-2">Enviar</button>
      </form>
    </main>
  )
}