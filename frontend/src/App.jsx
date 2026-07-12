import { lazy, Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import LoadingSpinner from './components/ui/LoadingSpinner';

import PublicLayout from './layouts/PublicLayout';
import ClientLayout from './layouts/ClientLayout';
import ExpertLayout from './layouts/ExpertLayout';
import AdminLayout from './layouts/AdminLayout';

import { ProtectedRoute, RoleRoute, GuestRoute } from './router/guards';

const LandingPage = lazy(() => import('./pages/public/LandingPage'));
const LoginPage = lazy(() => import('./pages/public/LoginPage'));
const RegisterPage = lazy(() => import('./pages/public/RegisterPage'));
const ForgotPasswordPage = lazy(() => import('./pages/public/ForgotPasswordPage'));
const ResetPasswordPage = lazy(() => import('./pages/public/ResetPasswordPage'));

const ClientDashboard = lazy(() => import('./pages/client/Dashboard'));
const JobMarketplace = lazy(() => import('./pages/client/JobMarketplace'));
const ClientTransactions = lazy(() => import('./pages/client/Transactions'));
const ClientSettings = lazy(() => import('./pages/client/Settings'));

const ExpertDashboard = lazy(() => import('./pages/expert/Dashboard'));
const ExpertProfile = lazy(() => import('./pages/expert/ExpertProfile'));
const BrowseJobs = lazy(() => import('./pages/expert/BrowseJobs'));
const WithdrawEarnings = lazy(() => import('./pages/expert/WithdrawEarnings'));
const ExpertSettings = lazy(() => import('./pages/expert/Settings'));

const AdminDashboard = lazy(() => import('./pages/admin/Dashboard'));
const UserManagement = lazy(() => import('./pages/admin/UserManagement'));
const MarketplaceManagement = lazy(() => import('./pages/admin/MarketplaceManagement'));
const DisputeManagement = lazy(() => import('./pages/admin/DisputeManagement'));
const AnalyticsReports = lazy(() => import('./pages/admin/AnalyticsReports'));
const AuditLogs = lazy(() => import('./pages/admin/AuditLogs'));

const JobDetail = lazy(() => import('./pages/shared/JobDetail'));
const ProjectList = lazy(() => import('./pages/shared/ProjectList'));
const ProjectWorkspace = lazy(() => import('./pages/shared/ProjectWorkspace'));

function RoleRedirect() {
  const user = useAuthStore((s) => s.user);
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'ADMIN') return <Navigate to="/admin/dashboard" replace />;
  if (user.role === 'EXPERT') return <Navigate to="/expert/dashboard" replace />;
  return <Navigate to="/client/dashboard" replace />;
}

export default function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        <Route element={<PublicLayout />}>
          <Route index element={<LandingPage />} />
          <Route element={<GuestRoute />}>
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
            <Route path="forgot-password" element={<ForgotPasswordPage />} />
            <Route path="reset-password" element={<ResetPasswordPage />} />
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
    </Suspense>
  );
}
