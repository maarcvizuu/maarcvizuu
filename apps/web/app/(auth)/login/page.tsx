"use client"
import { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { api } from '@/lib/api'

export default function LoginPage() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const res = await api.post('/auth/login', { email, password })
      localStorage.setItem('token', res.data.token)
      router.push('/app')
    } catch (err: any) {
      setError(err.response?.data?.error || 'Error al iniciar sesión')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="mx-auto max-w-md p-6">
      <h1 className="text-2xl font-semibold">Entrar</h1>
      <form onSubmit={onSubmit} className="mt-6 space-y-4">
        <input value={email} onChange={e=>setEmail(e.target.value)} type="email" placeholder="Email" className="w-full border rounded px-3 py-2" required />
        <input value={password} onChange={e=>setPassword(e.target.value)} type="password" placeholder="Contraseña" className="w-full border rounded px-3 py-2" required />
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button disabled={loading} className="w-full rounded bg-black text-white px-4 py-2">{loading? 'Entrando...' : 'Entrar'}</button>
      </form>
      <p className="text-sm text-gray-600 mt-4">¿No tienes cuenta? <Link href="/auth/register" className="underline">Regístrate</Link></p>
    </main>
  )
}