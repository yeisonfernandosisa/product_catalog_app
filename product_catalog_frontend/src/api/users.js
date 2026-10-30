import api from './apiClient'

export const getUsers = (params) => api.get('/users', { params }).then((response) => response.data)

export const updateUser = (id, data) =>
  api.put(`/users/${id}`, data).then((response) => response.data)
