import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useCurrentUser } from '../hooks/useAuth';
import LoadingSpinner from '../components/ui/LoadingSpinner';

export function ProtectedRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
  const { isLoading, isError } = useCurrentUser();

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (isLoading) return <LoadingSpinner label="Đang xác thực..." />;
  if (isError) return <Navigate to="/login" replace />;
  return <Outlet />;
}

export function RoleRoute({ role }) {
  const user = useAuthStore((s) => s.user);
  if (!user) return <LoadingSpinner />;
  if (user.role !== role) return <Navigate to="/" replace />;
  return <Outlet />;
}

export function GuestRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
  if (isAuthenticated) return <Navigate to="/" replace />;
  return <Outlet />;
}
