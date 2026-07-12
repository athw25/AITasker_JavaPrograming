import { apiClient, unwrap } from './client';

export const jobsApi = {
  getAll: () => apiClient.get('/jobs').then(unwrap),
  getMine: () => apiClient.get('/jobs/me').then(unwrap),
  getById: (id) => apiClient.get(`/jobs/${id}`).then(unwrap),
  create: (payload) => apiClient.post('/jobs', payload).then(unwrap),
  update: (id, payload) => apiClient.put(`/jobs/${id}`, payload).then(unwrap),
  remove: (id) => apiClient.delete(`/jobs/${id}`).then(unwrap),
  search: (params) => apiClient.get('/jobs/search', { params }).then(unwrap)
};
