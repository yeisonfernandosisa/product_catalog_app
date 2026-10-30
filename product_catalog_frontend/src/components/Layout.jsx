import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function Layout() {
  const { user, logout } = useAuth()
  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink className="brand" to="/">Catálogo</NavLink>
        <nav>
          <NavLink to="/">Productos</NavLink>
          <NavLink to="/categories">Categorías</NavLink>
          <NavLink to="/profile">Mi perfil</NavLink>
          {user?.role === 'ADMIN' && <NavLink to="/users">Usuarios</NavLink>}
        </nav>
        <div className="account">
          <span>{user?.fullName}</span>
          <button className="button button-light" onClick={logout}>Cerrar sesión</button>
        </div>
      </header>
      <main className="page"><Outlet /></main>
    </div>
  )
}
