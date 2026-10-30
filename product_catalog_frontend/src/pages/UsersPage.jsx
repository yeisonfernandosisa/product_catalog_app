import { useEffect, useState } from 'react'
import { getUsers, updateUser } from '../api/catalog'
import { errorMessage } from '../api/client'
import Pagination from '../components/Pagination'

export default function UsersPage() {
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 })
  const [page, setPage] = useState(0)
  const [selected, setSelected] = useState(null)
  const [form, setForm] = useState({})
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  async function load() {
    setLoading(true); setError('')
    try { setData(await getUsers({ page, size: 10, sort: 'fullName,asc' })) }
    catch (e) { setError(errorMessage(e)) }
    finally { setLoading(false) }
  }
  useEffect(() => { load() }, [page])
  function edit(user) { setSelected(user); setForm({ email: user.email, fullName: user.fullName, role: user.role, password: '' }) }
  async function submit(event) {
    event.preventDefault()
    try {
      const payload = { ...form }
      if (!payload.password) delete payload.password
      await updateUser(selected.id, payload); setSelected(null); await load()
    } catch (e) { setError(errorMessage(e)) }
  }
  return <>
    <div className="page-heading"><div><p className="eyebrow">ADMINISTRACIÓN</p><h1>Usuarios</h1><p className="muted">Consulta y actualiza perfiles y roles de usuario.</p></div></div>
    {error && <p className="error-message" role="alert">{error}</p>}
    {selected && <form className="panel product-form" onSubmit={submit}><h2>Editar usuario</h2><div className="form-grid">
      <label>Nombre<input required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label>
      <label>Correo<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
      <label>Rol<select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}><option>USER</option><option>ADMIN</option></select></label>
      <label>Nueva contraseña (opcional)<input type="password" minLength="8" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>
    </div><div className="form-actions"><button type="button" className="button button-light" onClick={() => setSelected(null)}>Cancelar</button><button className="button button-primary">Guardar usuario</button></div></form>}
    <section className="panel"><div className="table-toolbar"><h2>Usuarios <span className="count">{data.totalElements}</span></h2></div>
      {loading ? <p className="loading">Cargando usuarios…</p> : <div className="table-scroll"><table><thead><tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Acción</th></tr></thead><tbody>
        {data.content.map((user) => <tr key={user.id}><td><strong>{user.fullName}</strong></td><td>{user.email}</td><td><span className="tag">{user.role}</span></td><td><button className="text-button" onClick={() => edit(user)}>Ver / editar</button></td></tr>)}
      </tbody></table></div>}<Pagination page={page} totalPages={data.totalPages} onChange={setPage} />
    </section>
  </>
}
