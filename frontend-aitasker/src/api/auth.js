import { apiClient } from './client';

export const authApi = {
  register: (payload) => apiClient.post('/auth/register', payload).then((r) => r.data),
  login: (payload) => apiClient.post('/auth/login', payload).then((r) => r.data),
  refresh: (refreshToken) => apiClient.post('/auth/token/refresh', { refreshToken }).then((r) => r.data),
  logout: (refreshToken) => apiClient.post('/auth/token/revoke', { refreshToken }).then((r) => r.data),
  forgotPassword: (email) => apiClient.post('/auth/forgot-password', { email }).then((r) => r.data),
  resetPassword: (token, newPassword) => apiClient.post('/auth/reset-password', { token, newPassword }).then((r) => r.data),
  me: () => apiClient.get('/users/me').then((r) => r.data)
};
