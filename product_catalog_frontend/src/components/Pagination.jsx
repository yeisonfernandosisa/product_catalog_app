export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null
  return (
    <div className="flex flex-wrap items-center justify-center gap-3 pt-5 text-xs text-slate-500 sm:gap-4">
      <button
        className="rounded-lg bg-slate-100 px-3 py-2 font-semibold text-slate-700 transition hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-50"
        disabled={page === 0}
        onClick={() => onChange(page - 1)}
      >
        Anterior
      </button>
      <span>
        Página {page + 1} de {totalPages}
      </span>
      <button
        className="rounded-lg bg-slate-100 px-3 py-2 font-semibold text-slate-700 transition hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-50"
        disabled={page + 1 >= totalPages}
        onClick={() => onChange(page + 1)}
      >
        Siguiente
      </button>
    </div>
  )
}
