import { fireEvent, render, screen } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import UsersPage from '../../pages/UsersPage'

const { getUsers } = vi.hoisted(() => ({
  getUsers: vi.fn(),
}))

vi.mock('../../api/users', () => ({
  getUsers,
  updateUser: vi.fn(),
}))

describe('UsersPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    getUsers.mockResolvedValue({
      content: [
        {
          id: 1,
          email: 'user@example.com',
          fullName: 'Test User',
          role: 'USER',
        },
      ],
      totalPages: 1,
      totalElements: 1,
    })
  })

  it('focuses the user name when editing a user', async () => {
    render(<UsersPage />)
    await screen.findByText('Test User')

    fireEvent.click(screen.getByRole('button', { name: 'Ver / editar' }))

    expect(screen.getByLabelText('Nombre')).toHaveFocus()
  })
})
