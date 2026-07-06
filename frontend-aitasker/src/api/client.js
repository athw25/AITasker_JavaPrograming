import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const apiClient = axios.create({ baseURL: BASE_URL });

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

let refreshPromise = null;

async function refreshAccessToken() {
  const { refreshToken } = useAuthStore.getState();
  if (!refreshToken) throw new Error('No refresh token');

  const res = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken });
  const { token, refreshToken: newRefreshToken } = res.data;
  useAuthStore.getState().setAuth({ token, refreshToken: newRefreshToken });
  return token;
}

apiClient.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        if (!refreshPromise) refreshPromise = refreshAccessToken();
        const token = await refreshPromise;
        refreshPromise = null;
        original.headers.Authorization = `Bearer ${token}`;
        return apiClient(original);
      } catch (e) {
        refreshPromise = null;
        useAuthStore.getState().clear();
        window.location.href = '/login';
        return Promise.reject(e);
      }
    }
    return Promise.reject(error);
  }
);

export function unwrap(res) {
  return res.data?.data !== undefined ? res.data.data : res.data;
}

export function errorMessage(err) {
  return err?.response?.data?.message || err?.message || 'Đã xảy ra lỗi, vui lòng thử lại';
}
