import { apiClient, unwrap } from './client';

export const deliveriesApi = {
  submit: (payload) => apiClient.post('/deliveries', payload).then(unwrap),
  getByMilestone: (milestoneId) => apiClient.get(`/deliveries/milestone/${milestoneId}`).then(unwrap)
};
