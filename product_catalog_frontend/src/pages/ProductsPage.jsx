import { useCallback, useEffect, useRef, useState } from 'react'
import { getCategories } from '../api/categories'
import { errorMessage } from '../api/apiClient'
import { deleteProduct, getProducts, saveProduct } from '../api/products'
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
  const nameInputRef = useRef(null)

  const loadData = useCallback(async () => {
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
    loadData()
  }, [loadData])

  useEffect(() => {
    if (editing !== null && editing !== 'new') {
      nameInputRef.current?.focus()
    }
  }, [editing])

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
      await loadData()
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
      await loadData()
    } catch (requestError) {
      setError(errorMessage(requestError))
    }
  }

  return (
    <>
      <div className="mb-7 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="mb-2 text-[10px] font-bold tracking-[0.15em] text-slate-500">INVENTARIO</p>
          <h1 className="mb-2 font-display text-3xl font-extrabold tracking-tight text-slate-900">
            Productos
          </h1>
          <p className="text-sm text-slate-500">Administra tus productos y sus categorías.</p>
        </div>
        <button
          className="w-full rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 sm:w-auto"
          onClick={startCreate}
        >
          ＋ Nuevo producto
        </button>
      </div>

      {error && (
        <p className="mb-4 rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700" role="alert">
          {error}
        </p>
      )}

      {editing !== null && (
        <form
          className="mb-5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"
          onSubmit={submit}
        >
          <h2 className="mb-5 font-display text-lg font-bold text-slate-900">
            {editing === 'new' ? 'Nuevo producto' : 'Editar producto'}
          </h2>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Nombre
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                ref={nameInputRef}
                required
                value={form.name}
                onChange={(event) => setForm({ ...form, name: event.target.value })}
              />
            </label>
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Precio
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                type="number"
                min="0.01"
                step="0.01"
                required
                value={form.price}
                onChange={(event) => setForm({ ...form, price: event.target.value })}
              />
            </label>
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700 sm:col-span-2">
              Descripción
              <textarea
                className="min-h-20 w-full resize-y rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                value={form.description}
                onChange={(event) => setForm({ ...form, description: event.target.value })}
              />
            </label>
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700 sm:col-span-2">
              Categorías
              <select
                className="min-h-28 w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
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
              <small className="text-xs font-normal text-slate-500">
                Mantén Ctrl (Windows) o Cmd (Mac) para elegir varias.
              </small>
            </label>
          </div>
          <div className="mt-5 flex flex-col-reverse justify-end gap-2 sm:flex-row">
            <button
              type="button"
              className="w-full rounded-lg bg-slate-100 px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-200 sm:w-auto"
              onClick={() => setEditing(null)}
            >
              Cancelar
            </button>
            <button
              className="w-full rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50 sm:w-auto"
              disabled={saving}
            >
              {saving ? 'Guardando…' : 'Guardar producto'}
            </button>
          </div>
        </form>
      )}

      <section className="mb-5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
        <div className="mb-4 flex flex-col items-stretch justify-between gap-4 sm:flex-row sm:items-center">
          <h2 className="font-display text-lg font-bold text-slate-900">
            Listado{' '}
            <span className="ml-1 inline-flex h-6 min-w-6 items-center justify-center rounded-full bg-indigo-50 px-2 text-xs font-semibold text-indigo-700">
              {products.totalElements}
            </span>
          </h2>
          <label className="flex w-full flex-col gap-2 text-xs font-medium text-slate-600 sm:w-auto sm:flex-row sm:items-center">
            <span className="shrink-0">Filtrar por categoría</span>
            <select
              className="w-full min-w-0 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 sm:min-w-44"
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
          <p className="m-0 py-8 text-center text-sm text-slate-500">Cargando productos…</p>
        ) : products.content.length === 0 ? (
          <p className="m-0 py-8 text-center text-sm text-slate-500">
            No hay productos para mostrar.
          </p>
        ) : (
          <div className="w-full overflow-x-auto">
            <table className="w-full min-w-[640px] border-collapse text-left">
              <thead>
                <tr>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Producto
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Precio
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Categorías
                  </th>
                  <th className="border-b border-slate-200 bg-slate-50 px-3 py-3 text-[10px] font-bold uppercase tracking-wider text-slate-500">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody>
                {products.content.map((product) => (
                  <tr key={product.id}>
                    <td className="border-b border-slate-100 px-3 py-3 text-sm text-slate-600">
                      <strong className="font-semibold text-slate-900">{product.name}</strong>
                      <small className="mt-1 block max-w-[360px] truncate text-xs text-slate-400">
                        {product.description || 'Sin descripción'}
                      </small>
                    </td>
                    <td className="border-b border-slate-100 px-3 py-3 text-sm text-slate-600">
                      ${Number(product.price).toFixed(2)}
                    </td>
                    <td className="border-b border-slate-100 px-3 py-3">
                      <div className="flex flex-wrap gap-1">
                        {product.categories.map((category) => (
                          <span
                            className="inline-flex rounded-md bg-indigo-50 px-2 py-1 text-[10px] font-semibold text-indigo-700"
                            key={category.id}
                          >
                            {category.name}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="whitespace-nowrap border-b border-slate-100 px-3 py-3">
                      <button
                        className="px-1 py-1 text-xs font-semibold text-indigo-600 hover:text-indigo-800"
                        onClick={() => startEdit(product)}
                      >
                        Editar
                      </button>
                      <button
                        className="ml-2 px-1 py-1 text-xs font-semibold text-red-600 hover:text-red-800"
                        onClick={() => remove(product)}
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

        <Pagination page={page} totalPages={products.totalPages} onChange={setPage} />
      </section>
    </>
  )
}
