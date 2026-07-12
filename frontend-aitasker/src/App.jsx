import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';

import PublicLayout from './layouts/PublicLayout';
import ClientLayout from './layouts/ClientLayout';
import ExpertLayout from './layouts/ExpertLayout';
import AdminLayout from './layouts/AdminLayout';

import { ProtectedRoute, RoleRoute, GuestRoute } from './router/guards';

import LandingPage from './pages/public/LandingPage';
import LoginPage from './pages/public/LoginPage';
import RegisterPage from './pages/public/RegisterPage';
import ForgotPasswordPage from './pages/public/ForgotPasswordPage';

import ClientDashboard from './pages/client/Dashboard';
import JobMarketplace from './pages/client/JobMarketplace';
import ClientTransactions from './pages/client/Transactions';
import ClientSettings from './pages/client/Settings';

import ExpertDashboard from './pages/expert/Dashboard';
import ExpertProfile from './pages/expert/ExpertProfile';
import BrowseJobs from './pages/expert/BrowseJobs';
import WithdrawEarnings from './pages/expert/WithdrawEarnings';
import ExpertSettings from './pages/expert/Settings';

import AdminDashboard from './pages/admin/Dashboard';
import UserManagement from './pages/admin/UserManagement';
import MarketplaceManagement from './pages/admin/MarketplaceManagement';
import DisputeManagement from './pages/admin/DisputeManagement';
import AnalyticsReports from './pages/admin/AnalyticsReports';
import AuditLogs from './pages/admin/AuditLogs';

import JobDetail from './pages/shared/JobDetail';
import ProjectList from './pages/shared/ProjectList';
import ProjectWorkspace from './pages/shared/ProjectWorkspace';

function RoleRedirect() {
  const user = useAuthStore((s) => s.user);
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/admin/dashboard" replace />;
  if (user.role === 'EXPERT') return <Navigate to="/expert/dashboard" replace />;
  return <Navigate to="/client/dashboard" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route element={<PublicLayout />}>
        <Route index element={<LandingPage />} />
        <Route element={<GuestRoute />}>
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />
          <Route path="forgot-password" element={<ForgotPasswordPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route path="app" element={<RoleRedirect />} />

        <Route element={<RoleRoute role="CLIENT" />}>
          <Route path="client" element={<ClientLayout />}>
            <Route path="dashboard" element={<ClientDashboard />} />
            <Route path="jobs" element={<JobMarketplace />} />
            <Route path="jobs/:id" element={<JobDetail />} />
            <Route path="projects" element={<ProjectList role="CLIENT" />} />
            <Route path="projects/:id" element={<ProjectWorkspace />} />
            <Route path="transactions" element={<ClientTransactions />} />
            <Route path="settings" element={<ClientSettings />} />
          </Route>
        </Route>

        <Route element={<RoleRoute role="EXPERT" />}>
          <Route path="expert" element={<ExpertLayout />}>
            <Route path="dashboard" element={<ExpertDashboard />} />
            <Route path="profile" element={<ExpertProfile />} />
            <Route path="browse-jobs" element={<BrowseJobs />} />
            <Route path="jobs/:id" element={<JobDetail />} />
            <Route path="projects" element={<ProjectList role="EXPERT" />} />
            <Route path="projects/:id" element={<ProjectWorkspace />} />
            <Route path="earnings" element={<WithdrawEarnings />} />
            <Route path="settings" element={<ExpertSettings />} />
          </Route>
        </Route>

        <Route element={<RoleRoute role="ADMIN" />}>
          <Route path="admin" element={<AdminLayout />}>
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="users" element={<UserManagement />} />
            <Route path="marketplace" element={<MarketplaceManagement />} />
            <Route path="disputes" element={<DisputeManagement />} />
            <Route path="analytics" element={<AnalyticsReports />} />
            <Route path="audit-logs" element={<AuditLogs />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
