import { NavLink, Outlet } from 'react-router-dom'
import { errorMessage } from '../api/client'
import { useAuth } from '../auth/AuthContext'

export default function Layout() {
  const { user, logout } = useAuth()

  async function handleLogout() {
    try {
      await logout()
    } catch (error) {
      window.alert(
        `La sesión se cerró en este dispositivo, pero no se pudo invalidar en el servidor: ${errorMessage(error)}`,
      )
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink className="brand" to="/">
          Catálogo
        </NavLink>
        <nav>
          <NavLink to="/">Productos</NavLink>
          <NavLink to="/categories">Categorías</NavLink>
          <NavLink to="/profile">Mi perfil</NavLink>
          {user?.role === 'ADMIN' && <NavLink to="/users">Usuarios</NavLink>}
        </nav>
        <div className="account">
          <span>{user?.fullName}</span>
          <button className="button button-light" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      </header>
      <main className="page">
        <Outlet />
      </main>
    </div>
  )
}
