import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="min-h-screen">
      <header className="flex min-h-16 flex-wrap items-center gap-x-8 gap-y-3 border-b border-slate-200 bg-white px-4 py-3 sm:px-6 lg:flex-nowrap lg:px-8">
        <NavLink
          className="font-display text-xl font-extrabold tracking-tight text-slate-900 no-underline"
          to="/"
        >
          Catálogo
        </NavLink>
        <nav className="order-3 flex w-full flex-1 items-center gap-5 overflow-x-auto text-sm lg:order-none lg:w-auto lg:gap-6">
          <NavLink
            className={({ isActive }) =>
              `shrink-0 border-b-2 py-2 no-underline transition-colors ${isActive
                ? 'border-indigo-600 text-indigo-700'
                : 'border-transparent text-slate-500 hover:text-slate-900'
              }`
            }
            to="/"
          >
            Productos
          </NavLink>
          <NavLink
            className={({ isActive }) =>
              `shrink-0 border-b-2 py-2 no-underline transition-colors ${isActive
                ? 'border-indigo-600 text-indigo-700'
                : 'border-transparent text-slate-500 hover:text-slate-900'
              }`
            }
            to="/categories"
          >
            Categorías
          </NavLink>
          <NavLink
            className={({ isActive }) =>
              `shrink-0 border-b-2 py-2 no-underline transition-colors ${isActive
                ? 'border-indigo-600 text-indigo-700'
                : 'border-transparent text-slate-500 hover:text-slate-900'
              }`
            }
            to="/profile"
          >
            Mi perfil
          </NavLink>
          {user?.role === 'ADMIN' && (
            <NavLink
              className={({ isActive }) =>
                `shrink-0 border-b-2 py-2 no-underline transition-colors ${isActive
                  ? 'border-indigo-600 text-indigo-700'
                  : 'border-transparent text-slate-500 hover:text-slate-900'
                }`
              }
              to="/users"
            >
              Usuarios
            </NavLink>
          )}
        </nav>
        <div className="ml-auto flex items-center gap-3 text-xs text-slate-600 sm:gap-4 sm:text-sm">
          <span className="hidden max-w-40 truncate sm:inline">{user?.fullName}</span>
          <button
            className="rounded-lg bg-slate-100 px-3 py-2 font-semibold text-slate-700 transition hover:bg-slate-200"
            onClick={logout}
          >
            Cerrar sesión
          </button>
        </div>
      </header>
      <main className="mx-auto w-full max-w-[1180px] px-4 py-7 sm:px-6 sm:py-10">
        <Outlet />
      </main>
    </div>
  )
}
