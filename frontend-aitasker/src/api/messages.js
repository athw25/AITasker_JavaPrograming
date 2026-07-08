import { apiClient } from './client';

export const messagesApi = {
  getByProject: (projectId) => apiClient.get(`/messages/project/${projectId}`).then((r) => r.data)
};
