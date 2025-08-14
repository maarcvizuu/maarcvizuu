"use client"
import { useEffect, useState } from 'react'
import { api } from '@/lib/api'
import { Card } from '@/components/Card'

export default function AppHome() {
  const [profiles, setProfiles] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchMore()
  }, [])

  async function fetchMore() {
    setLoading(true)
    setError(null)
    try {
      const res = await api.get('/profiles/browse?limit=10')
      setProfiles(res.data.users)
    } catch (e: any) {
      setError(e.response?.data?.error || 'Error cargando perfiles')
    } finally {
      setLoading(false)
    }
  }

  async function swipe(id: string, liked: boolean) {
    await api.post('/swipes', { toUserId: id, liked })
    setProfiles(prev => prev.filter(p => p.id !== id))
  }

  return (
    <main>
      <h1 className="text-2xl font-semibold mb-4">Explorar</h1>
      {loading && <p>Cargando...</p>}
      {error && <p className="text-red-600 text-sm">{error}</p>}
      <div className="space-y-6">
        {profiles.map(p => (
          <Card key={p.id} id={p.id} name={p.name} photoUrl={p.photoUrl} bio={p.bio} sector={p.sector} onLike={(id)=>swipe(id, true)} onDislike={(id)=>swipe(id, false)} />
        ))}
      </div>
      {!loading && profiles.length === 0 && (
        <div className="text-gray-600">No hay más perfiles por ahora.</div>
      )}
    </main>
  )
}