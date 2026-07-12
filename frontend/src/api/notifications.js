import { apiClient, unwrap } from './client';

export const notificationsApi = {
  getAll: () => apiClient.get('/notifications').then(unwrap),
  markAsRead: (id) => apiClient.put(`/notifications/${id}/read`).then(unwrap)
};
