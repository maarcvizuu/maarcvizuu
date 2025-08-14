export default function NavLayout({ children }: { children: React.ReactNode }) {
  return (
    <div>
      <nav className="sticky top-0 bg-white border-b border-gray-200 z-10">
        <div className="max-w-5xl mx-auto px-4 py-3 flex gap-4">
          <a href="/explore" className="hover:underline">Explorar</a>
          <a href="/chat" className="hover:underline">Chat</a>
          <a href="/profile" className="hover:underline ml-auto">Mi perfil</a>
        </div>
      </nav>
      <div>{children}</div>
    </div>
  )
}