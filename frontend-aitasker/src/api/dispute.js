import { apiClient, unwrap } from './client';

export const disputeApi = {
  create: (payload) => apiClient.post('/disputes', payload).then(unwrap),
  // Trả về object phân trang { content, currentPage, pageSize, totalElements, totalPages, first, last }
  getAll: (status, page = 0, size = 20) =>
    apiClient.get('/disputes', { params: { status: status || undefined, page, size } }).then(unwrap),
  getById: (id) => apiClient.get(`/disputes/${id}`).then(unwrap),
  resolve: (id, payload) => apiClient.put(`/disputes/${id}/resolve`, payload).then(unwrap),
  addEvidence: (id, payload) => apiClient.post(`/disputes/${id}/evidence`, payload).then(unwrap),
  addMessage: (id, message) => apiClient.post(`/disputes/${id}/messages`, { message }).then(unwrap)
};
