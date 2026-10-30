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
  if (error.code === 'ERR_NETWORK') {
    return 'No se pudo conectar con la API. Comprueba que el backend esté en ejecución en http://localhost:8080.'
  }
  return error.response?.data?.message || error.message || 'Ocurrió un error inesperado'
}

export default api
