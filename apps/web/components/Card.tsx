"use client"
import Image from 'next/image'

export type ProfileCardProps = {
  id: string
  name: string
  photoUrl?: string | null
  bio?: string | null
  sector?: string | null
  onLike?: (id: string) => void
  onDislike?: (id: string) => void
}

export function Card({ id, name, photoUrl, bio, sector, onLike, onDislike }: ProfileCardProps) {
  return (
    <div className="rounded-xl border shadow-sm overflow-hidden bg-white">
      {photoUrl ? (
        <div className="relative w-full h-60">
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img alt={name} src={photoUrl} className="w-full h-full object-cover" />
        </div>
      ) : (
        <div className="w-full h-60 bg-gray-100 flex items-center justify-center text-gray-500">Sin foto</div>
      )}
      <div className="p-4">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold">{name}</h3>
          {sector && <span className="text-xs text-gray-500">{sector}</span>}
        </div>
        {bio && <p className="text-sm text-gray-700 mt-2 line-clamp-3">{bio}</p>}
        <div className="mt-4 flex gap-3 justify-center">
          <button onClick={()=>onDislike?.(id)} className="rounded-full border px-6 py-2">No</button>
          <button onClick={()=>onLike?.(id)} className="rounded-full bg-green-600 text-white px-6 py-2">Sí</button>
        </div>
      </div>
    </div>
  )
}