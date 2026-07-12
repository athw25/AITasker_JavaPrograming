import { apiClient, unwrap } from './client';

export const adminApi = {
  getUsers: () => apiClient.get('/admin/users').then(unwrap),
  banUser: (id) => apiClient.put(`/admin/users/${id}/ban`).then(unwrap),
  unbanUser: (id) => apiClient.put(`/admin/users/${id}/unban`).then(unwrap),
  getJobs: () => apiClient.get('/admin/jobs').then(unwrap),
  deleteJob: (id) => apiClient.delete(`/admin/jobs/${id}`).then(unwrap),
  getDashboard: () => apiClient.get('/admin/dashboard').then(unwrap),
  getAnalytics: () => apiClient.get('/admin/analytics').then(unwrap),
  getReports: () => apiClient.get('/admin/reports').then(unwrap),
  getPaymentTransactions: () => apiClient.get('/admin/payments/transactions').then(unwrap),
  getWithdrawals: (status = 'PENDING') =>
    apiClient.get('/admin/payments/withdrawals', { params: { status } }).then(unwrap),
  approveWithdrawal: (id) => apiClient.put(`/admin/payments/withdrawals/${id}/approve`).then(unwrap),
  getAuditLogs: () => apiClient.get('/admin/audit-logs').then(unwrap)
};
