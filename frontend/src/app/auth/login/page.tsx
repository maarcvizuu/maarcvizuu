'use client'

import { useState } from 'react'
import Link from 'next/link'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      })
      const data = await res.json()
      if (!res.ok) throw new Error(data.error || 'Error de login')
      localStorage.setItem('token', data.token)
      window.location.href = '/explore'
    } catch (err: any) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center p-6">
      <form onSubmit={onSubmit} className="card p-6 w-full max-w-sm">
        <h2 className="text-xl font-semibold mb-4">Iniciar sesión</h2>
        <input className="border rounded px-3 py-2 w-full mb-3" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
        <input className="border rounded px-3 py-2 w-full mb-3" type="password" placeholder="Contraseña" value={password} onChange={e => setPassword(e.target.value)} />
        {error && <p className="text-red-600 text-sm mb-3">{error}</p>}
        <button disabled={loading} className="btn w-full" type="submit">{loading ? 'Entrando...' : 'Entrar'}</button>
        <p className="text-sm text-gray-600 mt-4">¿No tienes cuenta? <Link className="text-brand underline" href="/auth/register">Regístrate</Link></p>
      </form>
    </main>
  )
}