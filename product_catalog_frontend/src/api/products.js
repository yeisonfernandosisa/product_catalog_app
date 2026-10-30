import api from './apiClient'

export const getProducts = (params) =>
  api.get('/products', { params }).then((response) => response.data)

export const saveProduct = (data, id) => {
  const request = id ? api.put(`/products/${id}`, data) : api.post('/products', data)

  return request.then((response) => response.data)
}

export const deleteProduct = (id) => api.delete(`/products/${id}`)
