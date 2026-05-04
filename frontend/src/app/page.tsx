import Link from 'next/link'

export default function Home() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center p-6">
      <h1 className="text-3xl font-semibold mb-4">Startup Match</h1>
      <p className="text-gray-600 mb-8">Conecta con otros emprendedores. Crea tu perfil y empieza a hacer match.</p>
      <div className="flex gap-3">
        <Link className="btn" href="/auth/register">Crear cuenta</Link>
        <Link className="btn" href="/auth/login">Iniciar sesión</Link>
      </div>
    </main>
  )
}