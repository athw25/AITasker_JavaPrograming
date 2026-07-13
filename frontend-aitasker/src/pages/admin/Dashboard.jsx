import { useQuery } from '@tanstack/react-query';
import { Users, Briefcase, FolderKanban, DollarSign, ShieldAlert, TrendingUp } from 'lucide-react';
import { adminApi } from '../../api/admin';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import StatCard from '../../components/ui/StatCard';
import { formatCurrency } from '../../utils/format';

export default function AdminDashboard() {
  const { data, isLoading } = useQuery({ queryKey: ['admin', 'dashboard'], queryFn: adminApi.getDashboard });

  if (isLoading) return <LoadingSpinner />;
  if (!data) return null;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Admin Dashboard</h1>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard icon={Users} label="Tổng người dùng" value={data.totalUsers} />
        <StatCard icon={Users} label="Clients / Experts" value={`${data.totalClients} / ${data.totalExperts}`} color="emerald" />
        <StatCard icon={Briefcase} label="Tổng Job" value={data.totalJobs} color="orange" />
        <StatCard icon={FolderKanban} label="Tổng Project" value={data.totalProjects} color="purple" />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard icon={TrendingUp} label="Tỷ lệ hoàn thành Project" value={`${data.projectCompletionRate}%`} color="emerald" />
        <StatCard icon={TrendingUp} label="Tỷ lệ chấp nhận Proposal" value={`${data.proposalAcceptanceRate}%`} />
        <StatCard icon={DollarSign} label="Tổng doanh thu đã giải ngân" value={formatCurrency(data.totalRevenue)} color="emerald" />
        <StatCard icon={ShieldAlert} label="Tranh chấp đang mở" value={`${data.openDisputes} / ${data.totalDisputes}`} color="orange" />
      </div>
    </div>
  );
}
