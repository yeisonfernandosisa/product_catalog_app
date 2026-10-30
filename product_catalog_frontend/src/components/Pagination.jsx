export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null
  return (
    <div className="pagination">
      <button className="button button-light" disabled={page === 0} onClick={() => onChange(page - 1)}>Anterior</button>
      <span>Página {page + 1} de {totalPages}</span>
      <button className="button button-light" disabled={page + 1 >= totalPages} onClick={() => onChange(page + 1)}>Siguiente</button>
    </div>
  )
}
