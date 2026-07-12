import { apiClient } from './client';

export const jobsApi = {
  getAll: () => apiClient.get('/jobs').then((r) => r.data),
  getMine: () => apiClient.get('/jobs/me').then((r) => r.data),
  getById: (id) => apiClient.get(`/jobs/${id}`).then((r) => r.data),
  create: (payload) => apiClient.post('/jobs', payload).then((r) => r.data),
  update: (id, payload) => apiClient.put(`/jobs/${id}`, payload).then((r) => r.data),
  remove: (id) => apiClient.delete(`/jobs/${id}`),
  search: (params) => apiClient.get('/jobs/search', { params }).then((r) => r.data)
};
