import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Briefcase, FolderKanban, Wallet, Search } from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import { projectsApi } from '../../api/projects';
import { withdrawalsApi } from '../../api/payments';
import { useAuthStore } from '../../store/authStore';
import StatCard from '../../components/ui/StatCard';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import Badge from '../../components/ui/Badge';
import { statusBadge, PROJECT_STATUS } from '../../utils/statusMaps';

export default function ExpertDashboard() {
  const user = useAuthStore((s) => s.user);
  const { data: jobsPage, isLoading: l1 } = useQuery({
    queryKey: ['jobs', 'all'],
    queryFn: () => jobsApi.getAll({ page: 0, size: 100 })
  });
  const jobs = jobsPage?.content ?? [];
  const { data: projects = [], isLoading: l2 } = useQuery({ queryKey: ['projects', 'EXPERT'], queryFn: projectsApi.getExpertProjects });
  const { data: withdrawals = [] } = useQuery({ queryKey: ['withdrawals', 'me'], queryFn: withdrawalsApi.getMine });

  if (l1 || l2) return <LoadingSpinner />;

  const openJobs = jobs.filter((j) => j.status === 'OPEN').length;
  const activeProjects = projects.filter((p) => p.status === 'ACTIVE').length;
  const approvedEarnings = withdrawals.filter((w) => w.status === 'APPROVED').reduce((s, w) => s + Number(w.amount), 0);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Chào mừng trở lại, {user?.name}!</h1>
        <p className="text-slate-500">Đây là tổng quan hoạt động của bạn.</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard icon={Briefcase} label="Job đang mở" value={openJobs} />
        <StatCard icon={FolderKanban} label="Dự án đang thực hiện" value={activeProjects} color="emerald" />
        <StatCard icon={Wallet} label="Thu nhập đã rút" value={`$${approvedEarnings}`} color="orange" />
      </div>

      <div className="flex flex-wrap gap-3">
        <Link to="/expert/browse-jobs" className="btn-primary"><Search size={16} /> Tìm Job</Link>
        <Link to="/expert/profile" className="btn-secondary">Cập nhật hồ sơ</Link>
      </div>

      <div className="card p-5">
        <h3 className="mb-4 font-semibold text-slate-900">Dự án gần đây</h3>
        <div className="space-y-3">
          {projects.slice(0, 5).map((p) => {
            const s = statusBadge(PROJECT_STATUS, p.status);
            return (
              <Link key={p.id} to={`/expert/projects/${p.id}`} className="flex items-center justify-between rounded-lg px-2 py-2 hover:bg-slate-50">
                <span className="text-sm font-medium text-slate-700">{p.jobTitle}</span>
                <Badge color={s.color}>{s.label}</Badge>
              </Link>
            );
          })}
          {projects.length === 0 && <p className="text-sm text-slate-400">Chưa có dự án nào.</p>}
        </div>
      </div>
    </div>
  );
}
