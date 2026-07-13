import { apiClient, unwrap } from './client';

export const paymentsApi = {
  deposit: (payload) => apiClient.post('/payments/deposit', payload).then(unwrap),
  release: (payload) => apiClient.put('/payments/release', payload).then(unwrap),
  getTransactions: (paymentId) => apiClient.get(`/payments/transactions/${paymentId}`).then(unwrap)
  // Lưu ý: Backend hiện chưa có API "GET /payments/transactions/me" và
  // "PUT /payments/{id}/refund" (hoàn tiền chỉ được thực hiện gián tiếp qua
  // luồng xử lý Dispute ở Admin). Không thêm các hàm gọi 2 endpoint này ở đây
  // để tránh gọi nhầm route không tồn tại; cần bổ sung API Backend tương ứng
  // trước khi FE có nhu cầu dùng.
};

export const withdrawalsApi = {
  request: (payload) => apiClient.post('/withdrawals', payload).then(unwrap),
  getMine: () => apiClient.get('/withdrawals/me').then(unwrap)
};
