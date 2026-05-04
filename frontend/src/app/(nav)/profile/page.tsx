'use client'

import { useEffect, useMemo, useState } from 'react'

type User = { id: string; email: string; name: string; photoUrl?: string | null; bio?: string | null; sector?: string | null; interests: string[] }

export default function ProfilePage() {
  const token = useMemo(() => (typeof window !== 'undefined' ? localStorage.getItem('token') : null), [])
  const [user, setUser] = useState<User | null>(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const run = async () => {
      try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/profile/me`, {
          headers: { Authorization: `Bearer ${token}` }
        })
        const data = await res.json()
        if (!res.ok) throw new Error('Error cargando perfil')
        setUser(data)
      } catch (e: any) {
        setError(e.message)
      }
    }
    if (token) run()
  }, [token])

  const onSave = async () => {
    if (!user) return
    setSaving(true)
    setError(null)
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/profile/me`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          name: user.name,
          photoUrl: user.photoUrl,
          bio: user.bio,
          sector: user.sector,
          interests: user.interests
        })
      })
      if (!res.ok) throw new Error('Error guardando')
    } catch (e: any) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  if (!token) {
    if (typeof window !== 'undefined') window.location.href = '/auth/login'
    return null
  }

  if (!user) return <main className="min-h-screen p-4">Cargando...</main>

  return (
    <main className="min-h-screen p-4 max-w-xl mx-auto">
      <h2 className="text-2xl font-semibold mb-4">Mi perfil</h2>
      <div className="card p-4 space-y-3">
        <label className="block">
          <span className="text-sm text-gray-600">Nombre</span>
          <input className="border rounded px-3 py-2 w-full" value={user.name} onChange={e => setUser({ ...user, name: e.target.value })} />
        </label>
        <label className="block">
          <span className="text-sm text-gray-600">Foto (URL)</span>
          <input className="border rounded px-3 py-2 w-full" value={user.photoUrl ?? ''} onChange={e => setUser({ ...user, photoUrl: e.target.value })} />
        </label>
        <label className="block">
          <span className="text-sm text-gray-600">Bio</span>
          <textarea className="border rounded px-3 py-2 w-full" rows={3} value={user.bio ?? ''} onChange={e => setUser({ ...user, bio: e.target.value })} />
        </label>
        <label className="block">
          <span className="text-sm text-gray-600">Sector</span>
          <input className="border rounded px-3 py-2 w-full" value={user.sector ?? ''} onChange={e => setUser({ ...user, sector: e.target.value })} />
        </label>
        <label className="block">
          <span className="text-sm text-gray-600">Intereses (separados por coma)</span>
          <input className="border rounded px-3 py-2 w-full" value={user.interests.join(', ')} onChange={e => setUser({ ...user, interests: e.target.value.split(',').map(s => s.trim()).filter(Boolean) })} />
        </label>
        {error && <p className="text-red-600 text-sm">{error}</p>}
        <button className="btn" disabled={saving} onClick={onSave}>{saving ? 'Guardando...' : 'Guardar'}</button>
      </div>
    </main>
  )
}