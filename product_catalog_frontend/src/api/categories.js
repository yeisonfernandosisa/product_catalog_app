import api from './apiClient'

export const getCategories = (params) =>
  api.get('/categories', { params }).then((response) => response.data)

export const saveCategory = (data, id) => {
  const request = id ? api.put(`/categories/${id}`, data) : api.post('/categories', data)

  return request.then((response) => response.data)
}

export const deleteCategory = (id) => api.delete(`/categories/${id}`)
