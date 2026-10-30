import { useEffect, useState } from 'react'
import { deleteCategory, getCategories, saveCategory } from '../api/catalog'
import { errorMessage } from '../api/client'
import Pagination from '../components/Pagination'

export default function CategoriesPage() {
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 })
  const [page, setPage] = useState(0)
  const [name, setName] = useState('')
  const [editing, setEditing] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  async function load() {
    setLoading(true); setError('')
    try { setData(await getCategories({ page, size: 10, sort: 'name,asc' })) }
    catch (e) { setError(errorMessage(e)) }
    finally { setLoading(false) }
  }
  useEffect(() => { load() }, [page])
  async function submit(event) {
    event.preventDefault(); setError('')
    try { await saveCategory({ name }, editing); setEditing(null); setName(''); await load() }
    catch (e) { setError(errorMessage(e)) }
  }
  function edit(category) { setEditing(category.id); setName(category.name) }
  async function remove(category) {
    if (!window.confirm(`¿Eliminar la categoría "${category.name}"?`)) return
    try { await deleteCategory(category.id); await load() } catch (e) { setError(errorMessage(e)) }
  }
  return <>
    <div className="page-heading"><div><p className="eyebrow">ORGANIZACIÓN</p><h1>Categorías</h1><p className="muted">Las categorías son privadas para tu cuenta.</p></div></div>
    {error && <p className="error-message" role="alert">{error}</p>}
    <form className="panel inline-form" onSubmit={submit}>
      <label>{editing ? 'Editar categoría' : 'Nueva categoría'}<input required value={name} onChange={(e) => setName(e.target.value)} placeholder="Ej. Electrónica" /></label>
      <button className="button button-primary">{editing ? 'Guardar cambios' : 'Crear categoría'}</button>
      {editing && <button type="button" className="button button-light" onClick={() => { setEditing(null); setName('') }}>Cancelar</button>}
    </form>
    <section className="panel"><div className="table-toolbar"><h2>Listado <span className="count">{data.totalElements}</span></h2></div>
      {loading ? <p className="loading">Cargando categorías…</p> : data.content.length === 0 ? <p className="empty-state">Aún no tienes categorías.</p> :
        <div className="table-scroll"><table><thead><tr><th>Nombre</th><th>Acciones</th></tr></thead><tbody>
          {data.content.map((category) => <tr key={category.id}><td><strong>{category.name}</strong></td><td className="actions"><button className="text-button" onClick={() => edit(category)}>Editar</button><button className="text-button danger" onClick={() => remove(category)}>Eliminar</button></td></tr>)}
        </tbody></table></div>}
      <Pagination page={page} totalPages={data.totalPages} onChange={setPage} />
    </section>
  </>
}
