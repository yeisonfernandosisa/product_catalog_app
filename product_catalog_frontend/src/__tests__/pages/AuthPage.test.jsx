import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import AuthPage from '../../pages/AuthPage'

const { login, setSession } = vi.hoisted(() => ({
  login: vi.fn(),
  setSession: vi.fn(),
}))

vi.mock('../../api/auth', () => ({
  login,
  register: vi.fn(),
}))

vi.mock('../../auth/AuthContext', () => ({
  useAuth: () => ({ user: null, setSession }),
}))

function renderLoginPage() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <Routes>
        <Route path="/login" element={<AuthPage mode="login" />} />
        <Route path="/" element={<p>Catálogo</p>} />
      </Routes>
    </MemoryRouter>,
  )
}

describe('AuthPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('shows an error when the password has fewer than eight characters', async () => {
    renderLoginPage()
    fireEvent.change(screen.getByLabelText('Contraseña'), {
      target: { value: 'short' },
    })

    fireEvent.submit(screen.getByRole('button', { name: 'Iniciar sesión' }).closest('form'))

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'La contraseña debe tener al menos 8 caracteres.',
    )
    expect(login).not.toHaveBeenCalled()
  })

  it('starts a session and opens the catalog after a successful login', async () => {
    const session = {
      token: 'test-token',
      email: 'admin@example.com',
      fullName: 'Test Admin',
      role: 'ADMIN',
    }
    login.mockResolvedValue(session)
    renderLoginPage()
    fireEvent.change(screen.getByLabelText('Correo electrónico'), {
      target: { value: 'admin@example.com' },
    })
    fireEvent.change(screen.getByLabelText('Contraseña'), {
      target: { value: 'Admin123!' },
    })

    fireEvent.click(screen.getByRole('button', { name: 'Iniciar sesión' }))

    await waitFor(() => expect(setSession).toHaveBeenCalledWith(session))
    expect(login).toHaveBeenCalledWith({
      email: 'admin@example.com',
      password: 'Admin123!',
      fullName: '',
    })
    expect(await screen.findByText('Catálogo')).toBeInTheDocument()
  })
})
