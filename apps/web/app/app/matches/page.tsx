"use client"
import { useEffect, useState } from 'react'
import Link from 'next/link'
import { api } from '@/lib/api'

export default function MatchesPage() {
  const [matches, setMatches] = useState<any[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/matches').then(res => setMatches(res.data.matches)).finally(()=>setLoading(false))
  }, [])

  return (
    <main>
      <h1 className="text-2xl font-semibold mb-4">Matches</h1>
      {loading && <p>Cargando...</p>}
      <ul className="divide-y">
        {matches.map(m => (
          <li key={m.id} className="py-3 flex items-center justify-between">
            <div>
              <div className="font-medium">{m.otherUser.name}</div>
              {m.lastMessage && <div className="text-sm text-gray-600">{m.lastMessage.text}</div>}
            </div>
            <Link href={`/app/chat/${m.id}`} className="text-blue-600">Abrir chat</Link>
          </li>
        ))}
      </ul>
    </main>
  )
}