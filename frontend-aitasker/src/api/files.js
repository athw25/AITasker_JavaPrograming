import { apiClient, unwrap } from './client';

export const filesApi = {
  // projectId: bắt buộc truyền khi file thuộc về 1 Project (Delivery, Milestone,
  // Project Attachment...) để Backend cho phép cả 2 phía Client/Expert của Project
  // tải xuống, không chỉ người upload. Bỏ trống nếu là file không gắn Project (Portfolio).
  upload: (file, projectId) => {
    const formData = new FormData();
    formData.append('file', file);
    if (projectId) formData.append('projectId', projectId);
    return apiClient
      .post('/files/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(unwrap);
  },
  // GET /api/files/{id} yêu cầu Authorization Bearer token, nên không thể dùng
  // trực tiếp như URL tĩnh trong thẻ <a href> (trình duyệt sẽ không gửi kèm
  // header và bị 401). Phải fetch qua apiClient rồi tự tạo blob URL để tải xuống.
  download: async (id, fileName) => {
    const res = await apiClient.get(`/files/${id}`, { responseType: 'blob' });
    const blobUrl = window.URL.createObjectURL(res.data);
    const link = document.createElement('a');
    link.href = blobUrl;
    link.download = fileName || `file-${id}`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(blobUrl);
  },
  remove: (id) => apiClient.delete(`/files/${id}`).then(unwrap)
};
