import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      token: null,
      refreshToken: null,
      user: null,
      setAuth: ({ token, refreshToken }) => set({ token, refreshToken }),
      setUser: (user) => set({ user }),
      clear: () => set({ token: null, refreshToken: null, user: null }),
      isAuthenticated: () => !!get().token,
      hasRole: (role) => get().user?.role === role
    }),
    { name: 'aitasker-auth' }
  )
);
