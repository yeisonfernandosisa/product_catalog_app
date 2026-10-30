import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const errorMessage = (error) => {
  if (error?.code === 'ERR_NETWORK') {
    return 'No se pudo conectar con la API. Comprueba que el backend esté en ejecución en http://localhost:8080.'
  }

  const message = error?.response?.data?.message
  if (typeof message === 'string' && message.trim()) {
    return message
  }

  const status = error?.response?.status
  if (status === 401) return 'Tu sesión ha caducado o no es válida. Inicia sesión nuevamente.'
  if (status === 403) return 'No tienes permisos para realizar esta acción.'
  if (status === 404) return 'No se encontró el recurso solicitado.'
  if (status === 409) return 'No se pudo completar la operación porque existe un conflicto.'
  if (status >= 500) return 'Ocurrió un error en el servidor. Inténtalo de nuevo más tarde.'
  if (status >= 400) return 'La solicitud no es válida. Revisa los datos e inténtalo de nuevo.'

  return 'Ocurrió un error inesperado. Inténtalo de nuevo.'
}

export default api
