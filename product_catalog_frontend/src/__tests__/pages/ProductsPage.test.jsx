import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import ProductsPage from '../../pages/ProductsPage'

const { getCategories, getProducts, saveProduct } = vi.hoisted(() => ({
  getCategories: vi.fn(),
  getProducts: vi.fn(),
  saveProduct: vi.fn(),
}))

vi.mock('../../api/categories', () => ({
  getCategories,
}))

vi.mock('../../api/products', () => ({
  getProducts,
  saveProduct,
  deleteProduct: vi.fn(),
}))

describe('ProductsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    getCategories.mockResolvedValue({
      content: [{ id: 1, name: 'Electrónica' }],
      totalPages: 1,
      totalElements: 1,
    })
    getProducts.mockResolvedValue({
      content: [
        {
          id: 1,
          name: 'Audífonos',
          description: 'Audífonos inalámbricos',
          price: 25,
          categories: [{ id: 1, name: 'Electrónica' }],
        },
      ],
      totalPages: 1,
      totalElements: 1,
    })
  })

  it('shows the products returned by the API', async () => {
    render(<ProductsPage />)

    expect(await screen.findByText('Audífonos')).toBeInTheDocument()
    expect(screen.getByText('$25.00')).toBeInTheDocument()
    expect(screen.getAllByText('Electrónica')).toHaveLength(2)
  })

  it('saves a new product with a numeric price', async () => {
    render(<ProductsPage />)
    fireEvent.click(await screen.findByRole('button', { name: /Nuevo producto/i }))
    fireEvent.change(screen.getByLabelText('Nombre'), {
      target: { value: 'Teclado' },
    })
    fireEvent.change(screen.getByLabelText('Precio'), {
      target: { value: '49.99' },
    })
    fireEvent.change(screen.getByLabelText('Descripción'), {
      target: { value: 'Teclado mecánico' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Guardar producto' }))

    await waitFor(() =>
      expect(saveProduct).toHaveBeenCalledWith(
        {
          name: 'Teclado',
          description: 'Teclado mecánico',
          price: 49.99,
          categoryIds: [],
        },
        null,
      ),
    )
  })
})
