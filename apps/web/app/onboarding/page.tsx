"use client"
import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { api } from '@/lib/api'

export default function OnboardingPage() {
  const router = useRouter()
  const [name, setName] = useState('')
  const [photoUrl, setPhotoUrl] = useState('')
  const [bio, setBio] = useState('')
  const [interests, setInterests] = useState('')
  const [sector, setSector] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    api.get('/profiles/me').then(res => {
      const u = res.data.user
      if (u) {
        setName(u.name || '')
        setPhotoUrl(u.photoUrl || '')
        setBio(u.bio || '')
        setInterests((u.interests || []).join(', '))
        setSector(u.sector || '')
      }
    }).catch(() => {})
  }, [])

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    try {
      await api.put('/profiles/me', {
        name, photoUrl, bio,
        interests: interests.split(',').map(s=>s.trim()).filter(Boolean),
        sector
      })
      router.push('/app')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="mx-auto max-w-xl p-6">
      <h1 className="text-2xl font-semibold">Completa tu perfil</h1>
      <form onSubmit={onSubmit} className="mt-6 space-y-4">
        <input value={name} onChange={e=>setName(e.target.value)} placeholder="Nombre" className="w-full border rounded px-3 py-2" />
        <input value={photoUrl} onChange={e=>setPhotoUrl(e.target.value)} placeholder="URL de foto" className="w-full border rounded px-3 py-2" />
        <textarea value={bio} onChange={e=>setBio(e.target.value)} placeholder="Bio" className="w-full border rounded px-3 py-2" />
        <input value={interests} onChange={e=>setInterests(e.target.value)} placeholder="Intereses (separados por coma)" className="w-full border rounded px-3 py-2" />
        <input value={sector} onChange={e=>setSector(e.target.value)} placeholder="Sector" className="w-full border rounded px-3 py-2" />
        <button disabled={loading} className="rounded bg-black text-white px-4 py-2">{loading ? 'Guardando...' : 'Guardar y continuar'}</button>
      </form>
    </main>
  )
}