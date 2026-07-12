import { apiClient } from './client';

export const authApi = {
  register: (payload) => apiClient.post('/auth/register', payload).then((r) => r.data),
  login: (payload) => apiClient.post('/auth/login', payload).then((r) => r.data),
  refresh: (refreshToken) => apiClient.post('/auth/refresh', { refreshToken }).then((r) => r.data),
  logout: (refreshToken) => apiClient.post('/auth/logout', { refreshToken }).then((r) => r.data),
  me: () => apiClient.get('/users/me').then((r) => r.data)
};
