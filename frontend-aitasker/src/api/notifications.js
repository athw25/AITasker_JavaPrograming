import { apiClient } from './client';

export const notificationsApi = {
  getAll: () => apiClient.get('/notifications').then((r) => r.data),
  markAsRead: (id) => apiClient.put(`/notifications/${id}/read`).then((r) => r.data)
};
