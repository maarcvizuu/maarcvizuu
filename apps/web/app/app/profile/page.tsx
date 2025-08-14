"use client"
import { useEffect, useState } from 'react'
import { api } from '@/lib/api'

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    api.get('/profiles/me').then(res => setUser(res.data.user))
  }, [])

  async function save() {
    setSaving(true)
    await api.put('/profiles/me', user)
    setSaving(false)
  }

  if (!user) return <main className="p-6">Cargando...</main>

  return (
    <main className="mx-auto max-w-xl p-6 space-y-4">
      <h1 className="text-2xl font-semibold">Mi perfil</h1>
      <input value={user.name || ''} onChange={e=>setUser({...user, name: e.target.value})} className="w-full border rounded px-3 py-2" placeholder="Nombre" />
      <input value={user.photoUrl || ''} onChange={e=>setUser({...user, photoUrl: e.target.value})} className="w-full border rounded px-3 py-2" placeholder="URL de foto" />
      <textarea value={user.bio || ''} onChange={e=>setUser({...user, bio: e.target.value})} className="w-full border rounded px-3 py-2" placeholder="Bio" />
      <input value={(user.interests || []).join(', ')} onChange={e=>setUser({...user, interests: e.target.value.split(',').map((s:string)=>s.trim()).filter(Boolean)})} className="w-full border rounded px-3 py-2" placeholder="Intereses (coma)" />
      <input value={user.sector || ''} onChange={e=>setUser({...user, sector: e.target.value})} className="w-full border rounded px-3 py-2" placeholder="Sector" />
      <button disabled={saving} onClick={save} className="rounded bg-black text-white px-4 py-2">{saving ? 'Guardando...' : 'Guardar'}</button>
    </main>
  )
}