import { useCallback, useEffect, useState } from 'react'
import { deleteProduct, getCategories, getProducts, saveProduct } from '../api/catalog'
import { errorMessage } from '../api/client'
import Pagination from '../components/Pagination'

const emptyForm = {
  name: '',
  description: '',
  price: '',
  categoryIds: [],
}

const emptyProductPage = {
  content: [],
  totalPages: 0,
  totalElements: 0,
}

async function loadAllCategories(firstPage) {
  const remainingPages = await Promise.all(
    Array.from({ length: Math.max(0, firstPage.totalPages - 1) }, (_, index) =>
      getCategories({
        page: index + 1,
        size: 100,
        sort: 'createdAt,desc',
      }),
    ),
  )

  return [firstPage, ...remainingPages].flatMap((categoryPage) => categoryPage.content)
}

export default function ProductsPage() {
  const [products, setProducts] = useState(emptyProductPage)
  const [categories, setCategories] = useState([])
  const [categoryId, setCategoryId] = useState('')
  const [page, setPage] = useState(0)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    setLoading(true)
    setError('')

    try {
      const [productPage, firstCategoryPage] = await Promise.all([
        getProducts({
          page,
          size: 8,
          sort: 'createdAt,desc',
          ...(categoryId ? { categoryId } : {}),
        }),
        getCategories({ page: 0, size: 100, sort: 'createdAt,desc' }),
      ])

      setProducts(productPage)
      setCategories(await loadAllCategories(firstCategoryPage))
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }, [page, categoryId])

  useEffect(() => {
    load()
  }, [load])

  function startEdit(product) {
    setEditing(product.id)
    setForm({
      name: product.name,
      description: product.description || '',
      price: product.price,
      categoryIds: product.categories.map((category) => category.id),
    })
  }

  function startCreate() {
    setEditing('new')
    setForm(emptyForm)
  }

  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    setError('')

    try {
      const product = {
        ...form,
        price: Number(form.price),
        categoryIds: form.categoryIds.map(Number),
      }

      await saveProduct(product, editing === 'new' ? null : editing)
      setEditing(null)
      await load()
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  async function remove(product) {
    if (!window.confirm(`¿Eliminar "${product.name}"?`)) {
      return
    }

    try {
      await deleteProduct(product.id)
      await load()
    } catch (requestError) {
      setError(errorMessage(requestError))
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">INVENTARIO</p>
          <h1>Productos</h1>
          <p className="muted">Administra tus productos y sus categorías.</p>
        </div>
        <button className="button button-primary" onClick={startCreate}>
          ＋ Nuevo producto
        </button>
      </div>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}

      {editing !== null && (
        <form className="panel product-form" onSubmit={submit}>
          <h2>{editing === 'new' ? 'Nuevo producto' : 'Editar producto'}</h2>
          <div className="form-grid">
            <label>
              Nombre
              <input
                required
                value={form.name}
                onChange={(event) => setForm({ ...form, name: event.target.value })}
              />
            </label>
            <label>
              Precio
              <input
                type="number"
                min="0.01"
                step="0.01"
                required
                value={form.price}
                onChange={(event) => setForm({ ...form, price: event.target.value })}
              />
            </label>
            <label className="span-two">
              Descripción
              <textarea
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </label>
            <label className="span-two">
              Categorías
              <select
                multiple
                value={form.categoryIds.map(String)}
                onChange={(event) =>
                  setForm({
                    ...form,
                    categoryIds: [...event.target.selectedOptions].map((option) => option.value),
                  })
                }
              >
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
              <small>Mantén Ctrl (Windows) o Cmd (Mac) para elegir varias.</small>
            </label>
          </div>
          <div className="form-actions">
            <button type="button" className="button button-light" onClick={() => setEditing(null)}>
              Cancelar
            </button>
            <button className="button button-primary" disabled={saving}>
              {saving ? 'Guardando…' : 'Guardar producto'}
            </button>
          </div>
        </form>
      )}

      <section className="panel">
        <div className="table-toolbar">
          <h2>
            Listado <span className="count">{products.totalElements}</span>
          </h2>
          <label className="filter-label">
            Filtrar por categoría
            <select
              value={categoryId}
              onChange={(event) => {
                setPage(0)
                setCategoryId(event.target.value)
              }}
            >
              <option value="">Todas las categorías</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>
        </div>

        {loading ? (
          <p className="loading">Cargando productos…</p>
        ) : products.content.length === 0 ? (
          <p className="empty-state">No hay productos para mostrar.</p>
        ) : (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Precio</th>
                  <th>Categorías</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {products.content.map((product) => (
                  <tr key={product.id}>
                    <td>
                      <strong>{product.name}</strong>
                      <small>{product.description || 'Sin descripción'}</small>
                    </td>
                    <td>${Number(product.price).toFixed(2)}</td>
                    <td>
                      <div className="tags">
                        {product.categories.map((category) => (
                          <span className="tag" key={category.id}>
                            {category.name}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="actions">
                      <button className="text-button" onClick={() => startEdit(product)}>
                        Editar
                      </button>
                      <button className="text-button danger" onClick={() => remove(product)}>
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <Pagination page={page} totalPages={products.totalPages} onChange={setPage} />
      </section>
    </>
  )
}
