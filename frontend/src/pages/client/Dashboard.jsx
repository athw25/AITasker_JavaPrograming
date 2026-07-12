import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Briefcase, FolderKanban, Bell, Plus, Search, Sparkles } from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import { projectsApi } from '../../api/projects';
import { notificationsApi } from '../../api/notifications';
import StatCard from '../../components/ui/StatCard';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import Badge from '../../components/ui/Badge';
import { statusBadge, PROJECT_STATUS } from '../../utils/statusMaps';
import { formatDate } from '../../utils/format';
import { useAuthStore } from '../../store/authStore';

export default function ClientDashboard() {
  const user = useAuthStore((s) => s.user);
  const { data: jobs = [], isLoading: l1 } = useQuery({ queryKey: ['myJobs'], queryFn: jobsApi.getMine });
  const { data: projects = [], isLoading: l2 } = useQuery({ queryKey: ['clientProjects'], queryFn: projectsApi.getClientProjects });
  const { data: notifications = [] } = useQuery({ queryKey: ['notifications'], queryFn: notificationsApi.getAll });

  if (l1 || l2) return <LoadingSpinner />;

  const openJobs = jobs.filter((j) => j.status === 'OPEN').length;
  const activeProjects = projects.filter((p) => p.status === 'ACTIVE').length;
  const unread = notifications.filter((n) => !n.isRead).length;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Chào mừng trở lại, {user?.name}!</h1>
        <p className="text-slate-500">Đây là tổng quan hoạt động của bạn.</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard icon={Briefcase} label="Job đang mở" value={openJobs} />
        <StatCard icon={FolderKanban} label="Dự án đang thực hiện" value={activeProjects} color="emerald" />
        <StatCard icon={Bell} label="Thông báo chưa đọc" value={unread} color="orange" />
      </div>

      <div className="flex flex-wrap gap-3">
        <Link to="/client/jobs" className="btn-primary"><Plus size={16} /> Đăng Job mới</Link>
        <Link to="/client/jobs" className="btn-secondary"><Search size={16} /> Xem Marketplace</Link>
        <Link to="/client/jobs" className="btn-ai"><Sparkles size={16} /> AI Job Assistant</Link>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="card p-5">
          <h3 className="mb-4 font-semibold text-slate-900">Job gần đây</h3>
          <div className="space-y-3">
            {jobs.slice(0, 5).map((j) => (
              <Link key={j.id} to={`/client/jobs/${j.id}`} className="flex items-center justify-between rounded-lg px-2 py-2 hover:bg-slate-50">
                <span className="text-sm font-medium text-slate-700">{j.title}</span>
                <Badge color={statusBadge({}, j.status).color}>{j.status}</Badge>
              </Link>
            ))}
            {jobs.length === 0 && <p className="text-sm text-slate-400">Chưa có job nào.</p>}
          </div>
        </div>

        <div className="card p-5">
          <h3 className="mb-4 font-semibold text-slate-900">Dự án gần đây</h3>
          <div className="space-y-3">
            {projects.slice(0, 5).map((p) => {
              const s = statusBadge(PROJECT_STATUS, p.status);
              return (
                <Link key={p.id} to={`/client/projects/${p.id}`} className="flex items-center justify-between rounded-lg px-2 py-2 hover:bg-slate-50">
                  <div>
                    <p className="text-sm font-medium text-slate-700">{p.jobTitle}</p>
                    <p className="text-xs text-slate-400">Expert: {p.expertName} · {formatDate(p.startDate)}</p>
                  </div>
                  <Badge color={s.color}>{s.label}</Badge>
                </Link>
              );
            })}
            {projects.length === 0 && <p className="text-sm text-slate-400">Chưa có dự án nào.</p>}
          </div>
        </div>
      </div>
    </div>
  );
}
