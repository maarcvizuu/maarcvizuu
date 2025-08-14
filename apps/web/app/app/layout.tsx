export default function AppLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen">
      <nav className="sticky top-0 z-10 bg-white/80 backdrop-blur border-b">
        <div className="mx-auto max-w-3xl p-4 flex items-center justify-between">
          <span className="font-semibold">Startup Match</span>
          <div className="text-sm text-gray-600">Explorar | Matches | Perfil</div>
        </div>
      </nav>
      <div className="mx-auto max-w-3xl p-4">{children}</div>
    </div>
  )
}