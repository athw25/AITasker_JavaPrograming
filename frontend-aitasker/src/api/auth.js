import { apiClient, unwrap } from './client';

export const authApi = {
  register: (payload) => apiClient.post('/auth/register', payload).then(unwrap),
  login: (payload) => apiClient.post('/auth/login', payload).then(unwrap),
  refresh: (refreshToken) => apiClient.post('/auth/refresh', { refreshToken }).then(unwrap),
  logout: (refreshToken) => apiClient.post('/auth/logout', { refreshToken }).then(unwrap),
  me: () => apiClient.get('/users/me').then(unwrap),
  updateMe: (payload) => apiClient.put('/users/me', payload).then(unwrap),
  forgotPassword: (email) => apiClient.post('/auth/forgot-password', { email }).then(unwrap),
  resetPassword: (token, newPassword) => apiClient.post('/auth/reset-password', { token, newPassword }).then(unwrap)
};
