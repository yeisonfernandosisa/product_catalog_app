import { useEffect, useState } from 'react'
import { errorMessage } from '../api/apiClient'
import { getProfile, updateProfile } from '../api/auth'
import { useAuth } from '../auth/AuthContext'

const emptyForm = {
  email: '',
  fullName: '',
  role: '',
  password: '',
}

export default function ProfilePage() {
  const { setSession } = useAuth()
  const [form, setForm] = useState(emptyForm)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    getProfile()
      .then((profile) => setForm((current) => ({ ...current, ...profile })))
      .catch((requestError) => setError(errorMessage(requestError)))
      .finally(() => setLoading(false))
  }, [])

  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    setMessage('')

    try {
      const session = await updateProfile({
        fullName: form.fullName,
        ...(form.password ? { password: form.password } : {}),
      })
      setSession(session)
      setForm((current) => ({
        ...current,
        fullName: session.fullName,
        password: '',
      }))
      setMessage('Tu perfil se actualizó correctamente.')
    } catch (requestError) {
      setError(errorMessage(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <div className="mb-7 flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <p className="mb-2 text-[10px] font-bold tracking-[0.15em] text-slate-500">CUENTA</p>
          <h1 className="mb-2 font-display text-3xl font-extrabold tracking-tight text-slate-900">
            Mi perfil
          </h1>
          <p className="text-sm text-slate-500">Administra tus datos de acceso.</p>
        </div>
      </div>

      <form
        className="grid max-w-[620px] gap-5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"
        onSubmit={submit}
      >
        {loading ? (
          <p className="m-0 py-8 text-center text-sm text-slate-500">Cargando perfil…</p>
        ) : (
          <>
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Correo electrónico
              <input
                className="w-full rounded-lg border border-slate-300 bg-slate-100 px-3 py-2.5 text-sm text-slate-500 disabled:cursor-not-allowed"
                type="email"
                value={form.email}
                disabled
              />
            </label>
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Nombre completo
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
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
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Rol
              <input
                className="w-full rounded-lg border border-slate-300 bg-slate-100 px-3 py-2.5 text-sm text-slate-500 disabled:cursor-not-allowed"
                value={form.role}
                disabled
              />
            </label>
            <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
              Nueva contraseña (opcional)
              <input
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-sm text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10"
                type="password"
                minLength="8"
                autoComplete="new-password"
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
              <p className="m-0 rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700" role="alert">
                {error}
              </p>
            )}
            {message && (
              <p
                className="m-0 rounded-lg bg-emerald-50 px-3 py-2.5 text-sm text-emerald-700"
                role="status"
              >
                {message}
              </p>
            )}

            <button
              className="w-full rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50 sm:w-fit"
              disabled={saving}
            >
              {saving ? 'Guardando…' : 'Guardar cambios'}
            </button>
          </>
        )}
      </form>
    </>
  )
}
