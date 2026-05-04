import './globals.css'
import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Startup Match',
  description: 'Conecta emprendedores con intereses afines',
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <body className="bg-gray-50 text-gray-900">
        {children}
      </body>
    </html>
  )
}