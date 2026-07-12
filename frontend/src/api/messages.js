import { apiClient, unwrap } from './client';

export const messagesApi = {
  getByProject: (projectId) => apiClient.get(`/messages/project/${projectId}`).then(unwrap)
};
