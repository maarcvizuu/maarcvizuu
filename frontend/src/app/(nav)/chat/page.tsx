'use client'

import { useEffect, useMemo, useState } from 'react'

type MatchItem = { id: string; other: { id: string; name: string; photoUrl?: string | null } }

type Message = { id: string; matchId: string; senderId: string; content: string; createdAt: string }

export default function ChatPage() {
  const token = useMemo(() => (typeof window !== 'undefined' ? localStorage.getItem('token') : null), [])
  const [matches, setMatches] = useState<MatchItem[]>([])
  const [activeMatch, setActiveMatch] = useState<MatchItem | null>(null)
  const [messages, setMessages] = useState<Message[]>([])
  const [input, setInput] = useState('')

  useEffect(() => {
    const run = async () => {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/chat/matches`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = await res.json()
      if (res.ok) setMatches(data)
    }
    if (token) run()
  }, [token])

  useEffect(() => {
    const load = async () => {
      if (!activeMatch) return
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/chat/messages/${activeMatch.id}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      const data = await res.json()
      if (res.ok) setMessages(data)
    }
    load()
    const t = setInterval(load, 2500)
    return () => clearInterval(t)
  }, [activeMatch, token])

  const send = async () => {
    if (!activeMatch || !input.trim()) return
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/chat/messages/${activeMatch.id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify({ content: input })
    })
    if (res.ok) {
      setInput('')
      const data = await res.json()
      setMessages(prev => [...prev, data])
    }
  }

  if (!token) {
    if (typeof window !== 'undefined') window.location.href = '/auth/login'
    return null
  }

  return (
    <main className="min-h-screen p-4 grid md:grid-cols-3 gap-4 max-w-5xl mx-auto">
      <aside className="card p-4">
        <h3 className="font-semibold mb-3">Matches</h3>
        <div className="space-y-2">
          {matches.map(m => (
            <button key={m.id} className={`w-full text-left p-2 rounded ${activeMatch?.id === m.id ? 'bg-brand text-white' : 'hover:bg-gray-100'}`} onClick={() => setActiveMatch(m)}>
              <div className="flex items-center gap-2">
                {/* eslint-disable-next-line @next/next/no-img-element */}
                <img src={m.other.photoUrl || `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(m.other.name)}`} alt={m.other.name} className="w-8 h-8 rounded-full" />
                <span>{m.other.name}</span>
              </div>
            </button>
          ))}
        </div>
      </aside>
      <section className="card p-4 md:col-span-2 flex flex-col">
        {activeMatch ? (
          <>
            <div className="flex-1 overflow-y-auto space-y-2">
              {messages.map(msg => (
                <div key={msg.id} className="p-2 bg-gray-100 rounded w-fit max-w-[80%]">
                  <p>{msg.content}</p>
                  <p className="text-[10px] text-gray-500 mt-1">{new Date(msg.createdAt).toLocaleTimeString()}</p>
                </div>
              ))}
            </div>
            <div className="mt-3 flex gap-2">
              <input value={input} onChange={e => setInput(e.target.value)} className="border rounded px-3 py-2 flex-1" placeholder="Escribe un mensaje" />
              <button className="btn" onClick={send}>Enviar</button>
            </div>
          </>
        ) : (
          <p className="text-gray-600">Selecciona un match para chatear.</p>
        )}
      </section>
    </main>
  )
}