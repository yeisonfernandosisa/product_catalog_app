import api from './apiClient'

export const login = (data) => api.post('/auth/login', data).then((response) => response.data)

export const register = (data) => api.post('/auth/register', data).then((response) => response.data)

export const logout = () => api.post('/auth/logout')

export const getProfile = () => api.get('/auth/profile').then((response) => response.data)

export const updateProfile = (data) => api.put('/auth/profile', data).then((response) => response.data)
