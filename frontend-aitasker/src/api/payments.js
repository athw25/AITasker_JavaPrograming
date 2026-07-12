import { apiClient, unwrap } from './client';

export const paymentsApi = {
  deposit: (payload) => apiClient.post('/payments/deposit', payload).then(unwrap),
  release: (payload) => apiClient.put('/payments/release', payload).then(unwrap),
  // Backend không có PUT /payments/{paymentId}/release riêng — dùng chung
  // /payments/release với body { paymentId } (đã khớp với PaymentController).
  releaseByPaymentId: (paymentId) => apiClient.put('/payments/release', { paymentId }).then(unwrap),
  getTransactions: (paymentId) => apiClient.get(`/payments/transactions/${paymentId}`).then(unwrap),
  getMyTransactions: () => apiClient.get('/payments/transactions/me').then(unwrap),
  refund: (id, reason) => apiClient.put(`/payments/${id}/refund`, null, { params: { reason } }).then(unwrap)
};

export const withdrawalsApi = {
  request: (payload) => apiClient.post('/withdrawals', payload).then(unwrap),
  getMine: () => apiClient.get('/withdrawals/me').then(unwrap)
};
