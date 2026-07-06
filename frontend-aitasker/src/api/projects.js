import { apiClient, unwrap } from './client';

export const projectsApi = {
  create: (payload) => apiClient.post('/projects', payload).then(unwrap),
  getById: (id) => apiClient.get(`/projects/${id}`).then(unwrap),
  getMine: () => apiClient.get('/projects/me').then(unwrap),
  getClientProjects: () => apiClient.get('/projects/client').then(unwrap),
  getExpertProjects: () => apiClient.get('/projects/expert').then(unwrap),
  update: (id, payload) => apiClient.put(`/projects/${id}`, payload).then(unwrap)
};
