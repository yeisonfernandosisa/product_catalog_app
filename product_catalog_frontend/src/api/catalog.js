import api from './client'

export const login = (data) => api.post('/auth/login', data).then((response) => response.data)
export const register = (data) => api.post('/auth/register', data).then((response) => response.data)
export const getProfile = () => api.get('/auth/me').then((response) => response.data)
export const updateProfile = (data) => api.put('/auth/me', data).then((response) => response.data)
export const getProducts = (params) => api.get('/products', { params }).then((response) => response.data)
export const saveProduct = (data, id) =>
  (id ? api.put(`/products/${id}`, data) : api.post('/products', data)).then((response) => response.data)
export const deleteProduct = (id) => api.delete(`/products/${id}`)
export const getCategories = (params) => api.get('/categories', { params }).then((response) => response.data)
export const saveCategory = (data, id) =>
  (id ? api.put(`/categories/${id}`, data) : api.post('/categories', data)).then((response) => response.data)
export const deleteCategory = (id) => api.delete(`/categories/${id}`)
export const getUsers = (params) => api.get('/users', { params }).then((response) => response.data)
export const updateUser = (id, data) => api.put(`/users/${id}`, data).then((response) => response.data)
