import { useCallback, useEffect, useState } from 'react'
import { deleteCategory, getCategories, saveCategory } from '../api/catalog'
import { errorMessage } from '../api/client'
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

  const load = useCallback(async () => {
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
    load()
  }, [load])

  async function submit(event) {
    event.preventDefault()
    setError('')

    try {
      await saveCategory({ name }, editing)
      setEditing(null)
      setName('')
      await load()
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
      await load()
    } catch (requestError) {
      setError(errorMessage(requestError))
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">ORGANIZACIÓN</p>
          <h1>Categorías</h1>
          <p className="muted">Las categorías son privadas para tu cuenta.</p>
        </div>
      </div>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}

      <form className="panel inline-form" onSubmit={submit}>
        <label>
          {editing ? 'Editar categoría' : 'Nueva categoría'}
          <input
            required
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="Ej. Electrónica"
          />
        </label>
        <button className="button button-primary">
          {editing ? 'Guardar cambios' : 'Crear categoría'}
        </button>
        {editing && (
          <button type="button" className="button button-light" onClick={cancelEdit}>
            Cancelar
          </button>
        )}
      </form>

      <section className="panel">
        <div className="table-toolbar">
          <h2>
            Listado <span className="count">{data.totalElements}</span>
          </h2>
        </div>

        {loading ? (
          <p className="loading">Cargando categorías…</p>
        ) : data.content.length === 0 ? (
          <p className="empty-state">Aún no tienes categorías.</p>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((category) => (
                  <tr key={category.id}>
                    <td>
                      <strong>{category.name}</strong>
                    </td>
                    <td className="actions">
                      <button className="text-button" onClick={() => edit(category)}>
                        Editar
                      </button>
                      <button className="text-button danger" onClick={() => remove(category)}>
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
