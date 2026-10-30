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
      <div className="mb-7 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <p className="mb-2 text-[10px] font-bold tracking-[0.15em] text-slate-500">
            ADMINISTRACIÓN
          </p>
          <h1 className="mb-2 font-display text-3xl font-extrabold tracking-tight text-slate-900">
            Usuarios
          </h1>
          <p className="text-sm text-slate-500">
            Consulta y actualiza perfiles y roles de usuario.
          </p>
        </div>
      </div>

      {error && (
        <p className="mb-4 rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700" role="alert">
          {error}
        </p>
      )}

      {selected && (
        <form
          className="mb-5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"
          onSubmit={submit}
        >
          <h2 className="mb-5 font-display text-lg font-bold text-slate-900">Editar usuario</h2>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Nombre
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
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
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Correo
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
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
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Rol
              <select
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
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
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Nueva contraseña (opcional)
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
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
          <div className="mt-5 flex flex-col-reverse justify-end gap-2 sm:flex-row">
            <button
              type="button"
              className="w-full rounded-lg bg-slate-100 px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-200 sm:w-auto"
              onClick={cancelEdit}
            >
              Cancelar
            </button>
            <button className="w-full rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700 sm:w-auto">
              Guardar usuario
            </button>
          </div>
        </form>
      )}

      <section className="mb-5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
        <div className="mb-4 flex items-center justify-between gap-3">
          <h2 className="font-display text-lg font-bold text-slate-900">
            Usuarios{' '}
            <span className="ml-1 inline-flex h-6 min-w-6 items-center justify-center rounded-full bg-indigo-50 px-2 text-xs font-semibold text-indigo-700">
              {data.totalElements}
            </span>
          </h2>
        </div>

        {loading ? (
          <p className="m-0 py-8 text-center text-sm text-slate-500">Cargando usuarios…</p>
        ) : (
          <div className="w-full overflow-x-auto">
            <table className="w-full min-w-[640px] border-collapse text-left">
              <thead>
                <tr>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Nombre
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Correo
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Rol
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Acción
                  </th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((user) => (
                  <tr key={user.id}>
                    <td className="border-b border-slate-100 px-3 py-3 text-sm text-slate-900">
                      <strong className="font-semibold">{user.fullName}</strong>
                    </td>
                    <td className="border-b border-slate-100 px-3 py-3 text-sm text-slate-600">
                      {user.email}
                    </td>
                    <td className="border-b border-slate-100 px-3 py-3">
                      <span className="inline-flex rounded-md bg-indigo-50 px-2 py-1 text-[10px] font-semibold text-indigo-700">
                        {user.role}
                      </span>
                    </td>
                    <td className="whitespace-nowrap border-b border-slate-100 px-3 py-3">
                      <button
                        className="px-1 py-1 text-xs font-semibold text-indigo-600 hover:text-indigo-800"
                        onClick={() => edit(user)}
                      >
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
