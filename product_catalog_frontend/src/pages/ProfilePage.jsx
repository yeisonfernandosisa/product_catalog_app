import { useEffect, useState } from 'react'
import { getProfile, updateProfile } from '../api/catalog'
import { errorMessage } from '../api/client'
import { useAuth } from '../auth/AuthContext'

export default function ProfilePage() {
  const { setSession } = useAuth()
  const [form, setForm] = useState({ email: '', fullName: '', role: '', password: '' })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  useEffect(() => {
    getProfile().then((profile) => setForm((current) => ({ ...current, ...profile })))
      .catch((requestError) => setError(errorMessage(requestError)))
      .finally(() => setLoading(false))
  }, [])
  async function submit(event) {
    event.preventDefault(); setSaving(true); setError(''); setMessage('')
    try {
      const session = await updateProfile({ fullName: form.fullName, ...(form.password ? { password: form.password } : {}) })
      setSession(session)
      setForm((current) => ({ ...current, fullName: session.fullName, password: '' }))
      setMessage('Tu perfil se actualizó correctamente.')
    } catch (requestError) { setError(errorMessage(requestError)) }
    finally { setSaving(false) }
  }
  return <>
    <div className="page-heading"><div><p className="eyebrow">CUENTA</p><h1>Mi perfil</h1><p className="muted">Administra tus datos de acceso.</p></div></div>
    <form className="panel profile-form" onSubmit={submit}>
      {loading ? <p className="loading">Cargando perfil…</p> : <>
        <label>Correo electrónico<input type="email" value={form.email} disabled /></label>
        <label>Nombre completo<input required value={form.fullName} onChange={(event) => setForm({ ...form, fullName: event.target.value })} /></label>
        <label>Rol<input value={form.role} disabled /></label>
        <label>Nueva contraseña (opcional)<input type="password" minLength="8" autoComplete="new-password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} /></label>
        {error && <p className="error-message" role="alert">{error}</p>}
        {message && <p className="success-message" role="status">{message}</p>}
        <button className="button button-primary" disabled={saving}>{saving ? 'Guardando…' : 'Guardar cambios'}</button>
      </>}
    </form>
  </>
}
