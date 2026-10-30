import { useCallback, useEffect, useState } from 'react'
import { getUsers, updateUser } from '../api/catalog'
import { errorMessage } from '../api/client'
import Pagination from '../components/Pagination'

const emptyPage = {
  content: [],
  totalPages: 0,
  totalElements: 0,
}

const emptyForm = {
  email: '',
  fullName: '',
  role: 'USER',
  password: '',
}

export default function UsersPage() {
  const [data, setData] = useState(emptyPage)
  const [page, setPage] = useState(0)
  const [selected, setSelected] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      setData(await getUsers({ page, size: 10, sort: 'createdAt,desc' }))
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }, [page])

  useEffect(() => {
    load()
  }, [load])

  function edit(user) {
    setSelected(user)
    setForm({
      email: user.email,
      fullName: user.fullName,
      role: user.role,
      password: '',
    })
  }

  function cancelEdit() {
    setSelected(null)
    setForm(emptyForm)
  }

  async function submit(event) {
    event.preventDefault()
    setError('')

    try {
      const payload = { ...form }
      if (!payload.password) {
        delete payload.password
      }

      await updateUser(selected.id, payload)
      cancelEdit()
      await load()
    } catch (requestError) {
      setError(errorMessage(requestError))
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">ADMINISTRACIÓN</p>
          <h1>Usuarios</h1>
          <p className="muted">Consulta y actualiza perfiles y roles de usuario.</p>
        </div>
      </div>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}

      {selected && (
        <form className="panel product-form" onSubmit={submit}>
          <h2>Editar usuario</h2>
          <div className="form-grid">
            <label>
              Nombre
              <input
                required
                value={form.fullName}
                onChange={(event) =>
                  setForm({
                    ...form,
                    fullName: event.target.value,
                  })
                }
              />
            </label>
            <label>
              Correo
              <input
                type="email"
                required
                value={form.email}
                onChange={(event) =>
                  setForm({
                    ...form,
                    email: event.target.value,
                  })
                }
              />
            </label>
            <label>
              Rol
              <select
                value={form.role}
                onChange={(event) =>
                  setForm({
                    ...form,
                    role: event.target.value,
                  })
                }
              >
                <option>USER</option>
                <option>ADMIN</option>
              </select>
            </label>
            <label>
              Nueva contraseña (opcional)
              <input
                type="password"
                minLength="8"
                value={form.password}
                onChange={(event) =>
                  setForm({
                    ...form,
                    password: event.target.value,
                  })
                }
              />
            </label>
          </div>
          <div className="form-actions">
            <button type="button" className="button button-light" onClick={cancelEdit}>
              Cancelar
            </button>
            <button className="button button-primary">Guardar usuario</button>
          </div>
        </form>
      )}

      <section className="panel">
        <div className="table-toolbar">
          <h2>
            Usuarios <span className="count">{data.totalElements}</span>
          </h2>
        </div>

        {loading ? (
          <p className="loading">Cargando usuarios…</p>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Correo</th>
                  <th>Rol</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((user) => (
                  <tr key={user.id}>
                    <td>
                      <strong>{user.fullName}</strong>
                    </td>
                    <td>{user.email}</td>
                    <td>
                      <span className="tag">{user.role}</span>
                    </td>
                    <td>
                      <button className="text-button" onClick={() => edit(user)}>
                        Ver / editar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <Pagination page={page} totalPages={data.totalPages} onChange={setPage} />
      </section>
    </>
  )
}
