import Link from 'next/link'

export default function HomePage() {
  return (
    <main className="mx-auto max-w-3xl p-6">
      <header className="py-8">
        <h1 className="text-3xl font-bold">Startup Match</h1>
        <p className="text-gray-600 mt-2">Conecta con emprendedores afines. Matchea y conversa al instante.</p>
      </header>
      <div className="mt-8 flex gap-4">
        <Link href="/register" className="rounded bg-black text-white px-4 py-2">Crear cuenta</Link>
        <Link href="/login" className="rounded border px-4 py-2">Entrar</Link>
      </div>
    </main>
  )
}