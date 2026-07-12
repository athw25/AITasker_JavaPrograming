import { apiClient, unwrap } from './client';

export const reviewsApi = {
  create: (payload) => apiClient.post('/reviews', payload).then(unwrap),
  getByUser: (userId) => apiClient.get(`/reviews/user/${userId}`).then(unwrap)
};
