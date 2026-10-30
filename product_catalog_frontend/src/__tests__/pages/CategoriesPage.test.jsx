import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import CategoriesPage from '../../pages/CategoriesPage'

const { getCategories, saveCategory } = vi.hoisted(() => ({
  getCategories: vi.fn(),
  saveCategory: vi.fn(),
}))

vi.mock('../../api/categories', () => ({
  getCategories,
  saveCategory,
  deleteCategory: vi.fn(),
}))

describe('CategoriesPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    getCategories.mockResolvedValue({
      content: [{ id: 1, name: 'Libros' }],
      totalPages: 1,
      totalElements: 1,
    })
  })

  it('shows the categories returned by the API', async () => {
    render(<CategoriesPage />)

    expect(await screen.findByText('Libros')).toBeInTheDocument()
  })

  it('creates a category using the name from the form', async () => {
    render(<CategoriesPage />)
    fireEvent.change(screen.getByLabelText('Nueva categoría'), {
      target: { value: 'Juguetes' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Crear categoría' }))

    await waitFor(() => expect(saveCategory).toHaveBeenCalledWith({ name: 'Juguetes' }, null))
  })
})
