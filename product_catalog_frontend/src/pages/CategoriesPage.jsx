import { useCallback, useEffect, useRef, useState } from 'react'
import { deleteCategory, getCategories, saveCategory } from '../api/categories'
import { errorMessage } from '../api/apiClient'
import Pagination from '../components/Pagination'

const emptyPage = {
  content: [],
  totalPages: 0,
  totalElements: 0,
}

export default function CategoriesPage() {
  const [data, setData] = useState(emptyPage)
  const [page, setPage] = useState(0)
  const [name, setName] = useState('')
  const [editing, setEditing] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const nameInputRef = useRef(null)

  const loadData = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      setData(await getCategories({ page, size: 10, sort: 'createdAt,desc' }))
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }, [page])

  useEffect(() => {
    loadData()
  }, [loadData])

  useEffect(() => {
    if (editing !== null) {
      nameInputRef.current?.focus()
    }
  }, [editing])

  async function submit(event) {
    event.preventDefault()
    setError('')

    try {
      await saveCategory({ name }, editing)
      setEditing(null)
      setName('')
      await loadData()
    } catch (requestError) {
      setError(errorMessage(requestError))
    }
  }

  function edit(category) {
    setEditing(category.id)
    setName(category.name)
  }

  function cancelEdit() {
    setEditing(null)
    setName('')
  }

  async function remove(category) {
    if (!window.confirm(`¿Eliminar la categoría "${category.name}"?`)) {
      return
    }

    try {
      await deleteCategory(category.id)
      await loadData()
    } catch (requestError) {
      setError(errorMessage(requestError))
    }
  }

  return (
    <>
      <div className="mb-7 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <p className="mb-2 text-[10px] font-bold tracking-[0.15em] text-slate-500">
            ORGANIZACIÓN
          </p>
          <h1 className="mb-2 font-display text-3xl font-extrabold tracking-tight text-slate-900">
            Categorías
          </h1>
          <p className="text-sm text-slate-500">Las categorías son privadas para tu cuenta.</p>
        </div>
      </div>

      {error && (
        <p className="mb-4 rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700" role="alert">
          {error}
        </p>
      )}

      <form
        className="mb-5 flex flex-col items-stretch gap-3 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-end sm:p-6"
        onSubmit={submit}
      >
        <label className="flex min-w-0 flex-1 flex-col gap-2 text-sm font-medium text-slate-700">
          {editing ? 'Editar categoría' : 'Nueva categoría'}
          <input
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
            ref={nameInputRef}
            required
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="Ej. Electrónica"
          />
        </label>
        <button className="w-full rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700 sm:w-auto">
          {editing ? 'Guardar cambios' : 'Crear categoría'}
        </button>
        {editing && (
          <button
            type="button"
            className="w-full rounded-lg bg-slate-100 px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-200 sm:w-auto"
            onClick={cancelEdit}
          >
            Cancelar
          </button>
        )}
      </form>

      <section className="mb-5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
        <div className="mb-4 flex items-center justify-between gap-3">
          <h2 className="font-display text-lg font-bold text-slate-900">
            Listado{' '}
            <span className="ml-1 inline-flex h-6 min-w-6 items-center justify-center rounded-full bg-indigo-50 px-2 text-xs font-semibold text-indigo-700">
              {data.totalElements}
            </span>
          </h2>
        </div>

        {loading ? (
          <p className="m-0 py-8 text-center text-sm text-slate-500">Cargando categorías…</p>
        ) : data.content.length === 0 ? (
          <p className="m-0 py-8 text-center text-sm text-slate-500">Aún no tienes categorías.</p>
        ) : (
          <div className="w-full overflow-x-auto">
            <table className="w-full min-w-[400px] border-collapse text-left">
              <thead>
                <tr>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Nombre
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((category) => (
                  <tr key={category.id}>
                    <td className="border-b border-slate-100 px-3 py-3 text-sm text-slate-900">
                      <strong className="font-semibold">{category.name}</strong>
                    </td>
                    <td className="whitespace-nowrap border-b border-slate-100 px-3 py-3">
                      <button
                        className="px-1 py-1 text-xs font-semibold text-indigo-600 hover:text-indigo-800"
                        onClick={() => edit(category)}
                      >
                        Editar
                      </button>
                      <button
                        className="ml-2 px-1 py-1 text-xs font-semibold text-red-600 hover:text-red-800"
                        onClick={() => remove(category)}
                      >
                        Eliminar
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
