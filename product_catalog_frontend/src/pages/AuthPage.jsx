import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { errorMessage } from '../api/apiClient'
import { login, register } from '../api/auth'
import { useAuth } from '../auth/AuthContext'

const emptyForm = {
  email: '',
  password: '',
  fullName: '',
}

export default function AuthPage({ mode }) {
  const isRegister = mode === 'register'
  const { user, setSession } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  if (user) {
    return <Navigate to="/" replace />
  }

  async function submit(event) {
    event.preventDefault()
    setError('')

    if (form.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres.')
      return
    }

    setLoading(true)
    try {
      const session = await (isRegister ? register(form) : login(form))
      setSession(session)
      navigate('/')
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="grid min-h-screen place-items-center bg-gradient-to-br from-indigo-50 via-slate-50 to-white px-4 py-8">
      <form
        className="w-full max-w-[420px] rounded-2xl border border-slate-200 bg-white p-6 shadow-xl shadow-slate-900/5 sm:p-9"
        onSubmit={submit}
      >
        <p className="mb-2 text-[10px] font-bold tracking-[0.15em] text-indigo-600">
          CATÁLOGO DE PRODUCTOS
        </p>
        <h1 className="mb-2 font-display text-2xl font-extrabold tracking-tight text-slate-900">
          {isRegister ? 'Crear cuenta' : 'Bienvenido de nuevo'}
        </h1>
        <p className="mb-6 text-sm leading-6 text-slate-500">
          {isRegister
            ? 'Crea tu espacio privado de catálogo.'
            : 'Inicia sesión para administrar tus productos.'}
        </p>

        {isRegister && (
          <label className="mb-4 flex flex-col gap-2 text-sm font-medium text-slate-700">
            Nombre completo
            <input
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
              required
              value={form.fullName}
              onChange={(event) =>
                setForm({
                  ...form,
                  fullName: event.target.value,
                })
              }
            />
          </label>
        )}

        <label className="mb-4 flex flex-col gap-2 text-sm font-medium text-slate-700">
          Correo electrónico
          <input
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
            type="email"
            required
            value={form.email}
            onChange={(event) =>
              setForm({
                ...form,
                email: event.target.value,
              })
            }
          />
        </label>
        <label className="mb-4 flex flex-col gap-2 text-sm font-medium text-slate-700">
          Contraseña
          <input
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
            type="password"
            minLength="8"
            required
            value={form.password}
            onChange={(event) =>
              setForm({
                ...form,
                password: event.target.value,
              })
            }
          />
        </label>

        {error && (
          <p className="mb-4 rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700" role="alert">
            {error}
          </p>
        )}

        <button
          className="mt-2 w-full rounded-lg bg-indigo-600 px-4 py-3 text-sm font-semibold text-white shadow-sm shadow-indigo-600/20 transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
          disabled={loading}
        >
          {loading ? 'Procesando…' : isRegister ? 'Registrarme' : 'Iniciar sesión'}
        </button>
        <p className="mt-5 text-center text-xs text-slate-500">
          {isRegister ? '¿Ya tienes una cuenta?' : '¿Aún no tienes cuenta?'}{' '}
          <Link
            className="font-semibold text-indigo-600 no-underline hover:text-indigo-700"
            to={isRegister ? '/login' : '/register'}
          >
            {isRegister ? 'Inicia sesión' : 'Regístrate'}
          </Link>
        </p>
      </form>
    </main>
  )
}
