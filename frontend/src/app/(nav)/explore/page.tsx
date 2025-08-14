'use client'

import { useEffect, useMemo, useState } from 'react'

type Candidate = { id: string; name: string; bio?: string | null; photoUrl?: string | null; sector?: string | null; interests: string[] }

export default function ExplorePage() {
  const [candidates, setCandidates] = useState<Candidate[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const token = useMemo(() => (typeof window !== 'undefined' ? localStorage.getItem('token') : null), [])

  useEffect(() => {
    const run = async () => {
      setLoading(true)
      setError(null)
      try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/swipe/candidates`, {
          headers: { Authorization: `Bearer ${token}` }
        })
        const data = await res.json()
        if (!res.ok) throw new Error('Error cargando candidatos')
        setCandidates(data)
      } catch (e: any) {
        setError(e.message)
      } finally {
        setLoading(false)
      }
    }
    if (token) run()
  }, [token])

  const swipe = async (toId: string, direction: 'left' | 'right') => {
    setCandidates(prev => prev.filter(c => c.id !== toId))
    if (direction === 'right') {
      try {
        await fetch(`${process.env.NEXT_PUBLIC_API_URL}/swipe/like/${toId}`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${token}` }
        })
      } catch {}
    }
  }

  if (!token) {
    if (typeof window !== 'undefined') window.location.href = '/auth/login'
    return null
  }

  return (
    <main className="min-h-screen p-4 max-w-xl mx-auto">
      <h2 className="text-2xl font-semibold mb-4">Explorar</h2>
      {loading && <p>Cargando...</p>}
      {error && <p className="text-red-600">{error}</p>}
      <div className="space-y-4">
        {candidates.map(c => (
          <div key={c.id} className="card p-4">
            <div className="flex items-center gap-3">
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img alt={c.name} src={c.photoUrl || `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(c.name)}`} className="w-14 h-14 rounded-full object-cover" />
              <div>
                <h3 className="font-semibold">{c.name}</h3>
                {c.bio && <p className="text-sm text-gray-600 line-clamp-2">{c.bio}</p>}
                {c.sector && <p className="text-xs text-gray-500 mt-1">{c.sector}</p>}
              </div>
            </div>
            {c.interests?.length > 0 && (
              <div className="mt-3 flex flex-wrap gap-2">
                {c.interests.map((i, idx) => (
                  <span key={idx} className="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded-full">{i}</span>
                ))}
              </div>
            )}
            <div className="flex gap-3 mt-4">
              <button className="btn bg-gray-200 text-gray-800 hover:bg-gray-300" onClick={() => swipe(c.id, 'left')}>No</button>
              <button className="btn" onClick={() => swipe(c.id, 'right')}>Me interesa</button>
            </div>
          </div>
        ))}
        {!loading && candidates.length === 0 && <p className="text-gray-600">No hay más perfiles por ahora.</p>}
      </div>
    </main>
  )
}