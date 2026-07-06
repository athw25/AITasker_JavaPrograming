import { apiClient, unwrap } from './client';

export const expertsApi = {
  getMyProfile: () => apiClient.get('/experts/me').then(unwrap),
  updateMyProfile: (payload) => apiClient.put('/experts/me', payload).then(unwrap)
};

export const portfolioApi = {
  add: (payload) => apiClient.post('/experts/portfolio', payload).then(unwrap),
  getByExpert: (expertId) => apiClient.get(`/experts/portfolio/${expertId}`).then(unwrap),
  remove: (id) => apiClient.delete(`/experts/portfolio/${id}`).then(unwrap)
};

export const servicePackagesApi = {
  getAll: () => apiClient.get('/services').then(unwrap),
  create: (payload) => apiClient.post('/services', payload).then(unwrap),
  update: (id, payload) => apiClient.put(`/services/${id}`, payload).then(unwrap),
  remove: (id) => apiClient.delete(`/services/${id}`).then(unwrap)
};
