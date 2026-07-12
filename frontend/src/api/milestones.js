import { apiClient, unwrap } from './client';

export const milestonesApi = {
  create: (payload) => apiClient.post('/milestones', payload).then(unwrap),
  update: (id, payload) => apiClient.put(`/milestones/${id}`, payload).then(unwrap),
  submit: (id, payload) => apiClient.put(`/milestones/${id}/submit`, payload).then(unwrap),
  approve: (id) => apiClient.put(`/milestones/${id}/approve`).then(unwrap),
  reject: (id, reason) =>
    apiClient.put(`/milestones/${id}/reject`, null, { params: { reason } }).then(unwrap),
  releasePayment: (id) => apiClient.put(`/milestones/${id}/release-payment`).then(unwrap),
  getByProject: (projectId) => apiClient.get(`/milestones/project/${projectId}`).then(unwrap)
};
