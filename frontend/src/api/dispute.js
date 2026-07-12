import { apiClient, unwrap } from './client';

export const disputeApi = {
  create: (payload) => apiClient.post('/disputes', payload).then(unwrap),
  getAll: (status) => apiClient.get('/disputes', { params: status ? { status } : {} }).then(unwrap),
  getById: (id) => apiClient.get(`/disputes/${id}`).then(unwrap),
  resolve: (id, payload) => apiClient.put(`/disputes/${id}/resolve`, payload).then(unwrap)
};
