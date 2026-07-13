import { apiClient, unwrap } from './client';

export const aiApi = {
  jobAssistant: (prompt) => apiClient.post('/ai/job-assistant', null, { params: { prompt } }).then(unwrap),
  serviceGenerator: (prompt) => apiClient.post('/ai/service-generator', null, { params: { prompt } }).then(unwrap),
  recommendExperts: (jobId) => apiClient.get(`/ai/recommend-experts/${jobId}`).then(unwrap)
};
