import { apiClient } from './client';

export const reviewsApi = {
  create: (payload) => apiClient.post('/reviews', payload).then((r) => r.data),
  getByUser: (userId) => apiClient.get(`/reviews/user/${userId}`).then((r) => r.data)
};
