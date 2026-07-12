import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { authApi } from '../api/auth';
import { useAuthStore } from '../store/authStore';
import { errorMessage } from '../api/client';

export function useCurrentUser() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
  const setUser = useAuthStore((s) => s.setUser);

  return useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const data = await authApi.me();
      setUser(data);
      return data;
    },
    enabled: isAuthenticated,
    staleTime: 5 * 60 * 1000,
    retry: false
  });
}

export function useLogin() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: authApi.login,
    onSuccess: async (data) => {
      setAuth(data);
      const me = await authApi.me();
      useAuthStore.getState().setUser(me);
      queryClient.setQueryData(['currentUser'], me);
      toast.success('Đăng nhập thành công!');
      if (me.role === 'ADMIN') navigate('/admin/dashboard');
      else if (me.role === 'EXPERT') navigate('/expert/dashboard');
      else navigate('/client/dashboard');
    },
    onError: (err) => toast.error(errorMessage(err))
  });
}

export function useRegister() {
  const navigate = useNavigate();
  return useMutation({
    mutationFn: authApi.register,
    onSuccess: () => {
      toast.success('Đăng ký thành công! Vui lòng đăng nhập.');
      navigate('/login');
    },
    onError: (err) => toast.error(errorMessage(err))
  });
}

export function useLogout() {
  const navigate = useNavigate();
  const clear = useAuthStore((s) => s.clear);
  const refreshToken = useAuthStore((s) => s.refreshToken);
  const queryClient = useQueryClient();

  return () => {
    if (refreshToken) authApi.logout(refreshToken).catch(() => {});
    clear();
    queryClient.clear();
    navigate('/login');
  };
}
