import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { errorMessage } from '../api/client'
import { login, register } from '../api/catalog'

export default function AuthPage({ mode }) {
  const isRegister = mode === 'register'
  const { user, setSession } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', fullName: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  if (user) return <Navigate to="/" replace />

  async function submit(event) {
    event.preventDefault()
    setError('')
    if (form.password.length < 8) return setError('La contraseña debe tener al menos 8 caracteres.')
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
    <main className="auth-page">
      <form className="auth-card" onSubmit={submit}>
        <p className="eyebrow">PRODUCT CATALOG</p>
        <h1>{isRegister ? 'Crear cuenta' : 'Bienvenido de nuevo'}</h1>
        <p className="muted">{isRegister ? 'Crea tu espacio privado de catálogo.' : 'Inicia sesión para administrar tus productos.'}</p>
        {isRegister && <label>Nombre completo<input required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label>}
        <label>Correo electrónico<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
        <label>Contraseña<input type="password" minLength="8" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>
        {error && <p className="error-message" role="alert">{error}</p>}
        <button className="button button-primary full-width" disabled={loading}>{loading ? 'Procesando…' : isRegister ? 'Registrarme' : 'Iniciar sesión'}</button>
        <p className="auth-switch">{isRegister ? '¿Ya tienes una cuenta?' : '¿Aún no tienes cuenta?'}{' '}
          <Link to={isRegister ? '/login' : '/register'}>{isRegister ? 'Inicia sesión' : 'Regístrate'}</Link>
        </p>
      </form>
    </main>
  )
}
