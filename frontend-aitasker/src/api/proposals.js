import { apiClient, unwrap } from './client';

export const proposalsApi = {
  create: (payload) => apiClient.post('/proposals', payload).then(unwrap),
  getByJob: (jobId, page = 0, size = 20) =>
    apiClient.get(`/proposals/job/${jobId}`, { params: { page, size } }).then(unwrap),
  getMine: () => apiClient.get('/proposals/me').then(unwrap),
  getDetail: (id) => apiClient.get(`/proposals/${id}`).then(unwrap),
  accept: (id) => apiClient.put(`/proposals/${id}/accept`).then(unwrap),
  reject: (id) => apiClient.put(`/proposals/${id}/reject`).then(unwrap),
  withdraw: (id) => apiClient.put(`/proposals/${id}/withdraw`).then(unwrap)
};
