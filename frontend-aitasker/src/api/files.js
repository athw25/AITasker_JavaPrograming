import { apiClient, unwrap } from './client';

export const filesApi = {
  upload: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient
      .post('/files/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
      .then(unwrap);
  },
  downloadUrl: (id) => `${apiClient.defaults.baseURL}/files/${id}`,
  remove: (id) => apiClient.delete(`/files/${id}`).then(unwrap)
};
