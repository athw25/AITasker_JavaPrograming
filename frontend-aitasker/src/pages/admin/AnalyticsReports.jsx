import { useQuery } from '@tanstack/react-query';
import { Sparkles, TrendingUp } from 'lucide-react';
import { adminApi } from '../../api/admin';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import StatCard from '../../components/ui/StatCard';
import { formatCurrency } from '../../utils/format';

export default function AnalyticsReports() {
  const { data: dashboard, isLoading: l1 } = useQuery({ queryKey: ['admin', 'dashboard'], queryFn: adminApi.getDashboard });
  const { data: analytics, isLoading: l2 } = useQuery({ queryKey: ['admin', 'analytics'], queryFn: adminApi.getAnalytics });

  if (l1 || l2) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Analytics & Reports</h1>

      <div>
        <h3 className="mb-3 font-semibold text-slate-900">Chỉ số nghiên cứu (Research Metrics)</h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <StatCard icon={TrendingUp} label="Tỷ lệ hoàn thành Project" value={`${dashboard.projectCompletionRate}%`} color="emerald" />
          <StatCard icon={TrendingUp} label="Tỷ lệ chấp nhận Proposal" value={`${dashboard.proposalAcceptanceRate}%`} />
          <StatCard icon={TrendingUp} label="Doanh thu đã giải ngân" value={formatCurrency(dashboard.totalRevenue)} color="purple" />
        </div>
      </div>

      <div>
        <h3 className="mb-3 flex items-center gap-2 font-semibold text-slate-900"><Sparkles size={18} className="text-ai-600" /> Mức độ sử dụng AI</h3>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-4">
          {Object.entries(analytics || {}).map(([key, value]) => (
            <StatCard key={key} label={key.replaceAll('_', ' ')} value={value} color="purple" />
          ))}
        </div>
      </div>
    </div>
  );
}
