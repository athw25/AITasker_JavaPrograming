import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Trash2, CheckCircle2 } from 'lucide-react';
import { adminApi } from '../../api/admin';
import { errorMessage } from '../../api/client';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { statusBadge, JOB_STATUS, WITHDRAWAL_STATUS } from '../../utils/statusMaps';
import { formatCurrency, formatDate, formatDateTime } from '../../utils/format';

const TABS = ['jobs', 'withdrawals', 'payments'];

export default function MarketplaceManagement() {
  const [tab, setTab] = useState('jobs');
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Marketplace Management</h1>
      <div className="flex gap-1 border-b border-slate-200">
        {TABS.map((t) => (
          <button key={t} onClick={() => setTab(t)}
            className={`border-b-2 px-4 py-2.5 text-sm font-medium capitalize ${tab === t ? 'border-primary-600 text-primary-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}>
            {{ jobs: 'Jobs', withdrawals: 'Withdrawals', payments: 'Payments' }[t]}
          </button>
        ))}
      </div>
      {tab === 'jobs' && <JobsTab />}
      {tab === 'withdrawals' && <WithdrawalsTab />}
      {tab === 'payments' && <PaymentsTab />}
    </div>
  );
}

function JobsTab() {
  const queryClient = useQueryClient();
  const [deleteTarget, setDeleteTarget] = useState(null);
  const { data: jobsPage, isLoading } = useQuery({ queryKey: ['admin', 'jobs'], queryFn: () => adminApi.getJobs({ page: 0, size: 100 }) });
  const jobs = jobsPage?.content ?? [];

  const deleteMutation = useMutation({
    mutationFn: adminApi.deleteJob,
    onSuccess: () => { toast.success('Đã xóa job'); setDeleteTarget(null); queryClient.invalidateQueries({ queryKey: ['admin', 'jobs'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;
  if (jobs.length === 0) return <EmptyState title="Không có job nào" />;

  return (
    <div className="card overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
          <tr><th className="px-5 py-3">Tiêu đề</th><th className="px-5 py-3">Ngân sách</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3 text-right">Thao tác</th></tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {jobs.map((j) => {
            const s = statusBadge(JOB_STATUS, j.status);
            return (
              <tr key={j.id} className="hover:bg-slate-50">
                <td className="px-5 py-3 font-medium">{j.title}</td>
                <td className="px-5 py-3">{formatCurrency(j.budget)}</td>
                <td className="px-5 py-3"><Badge color={s.color}>{s.label}</Badge></td>
                <td className="px-5 py-3 text-right">
                  <button className="btn-danger !px-3" onClick={() => setDeleteTarget(j)}><Trash2 size={14} /> Xóa</button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      <ConfirmDialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} danger title="Xóa Job"
        message={`Xóa job "${deleteTarget?.title}"?`} loading={deleteMutation.isPending} onConfirm={() => deleteMutation.mutate(deleteTarget.id)} />
    </div>
  );
}

function WithdrawalsTab() {
  const queryClient = useQueryClient();
  const [status, setStatus] = useState('PENDING');
  const { data: withdrawals = [], isLoading } = useQuery({ queryKey: ['admin', 'withdrawals', status], queryFn: () => adminApi.getWithdrawals(status) });

  const approveMutation = useMutation({
    mutationFn: adminApi.approveWithdrawal,
    onSuccess: () => { toast.success('Đã duyệt withdrawal'); queryClient.invalidateQueries({ queryKey: ['admin', 'withdrawals'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  return (
    <div className="space-y-4">
      <select className="input w-48" value={status} onChange={(e) => setStatus(e.target.value)}>
        <option value="PENDING">Chờ duyệt</option>
        <option value="APPROVED">Đã duyệt</option>
        <option value="REJECTED">Bị từ chối</option>
      </select>
      {isLoading ? <LoadingSpinner /> : withdrawals.length === 0 ? <EmptyState title="Không có yêu cầu nào" /> : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr><th className="px-5 py-3">Expert</th><th className="px-5 py-3">Số tiền</th><th className="px-5 py-3">Ngày yêu cầu</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3 text-right">Thao tác</th></tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {withdrawals.map((w) => {
                const s = statusBadge(WITHDRAWAL_STATUS, w.status);
                return (
                  <tr key={w.id}>
                    <td className="px-5 py-3">{w.expert?.name || w.expert?.email || `#${w.expert?.id}`}</td>
                    <td className="px-5 py-3 font-medium">{formatCurrency(w.amount)}</td>
                    <td className="px-5 py-3">{formatDateTime(w.requestedAt)}</td>
                    <td className="px-5 py-3"><Badge color={s.color}>{s.label}</Badge></td>
                    <td className="px-5 py-3 text-right">
                      {w.status === 'PENDING' && (
                        <button className="btn-success !px-3" onClick={() => approveMutation.mutate(w.id)}><CheckCircle2 size={14} /> Duyệt</button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function PaymentsTab() {
  const { data: transactions = [], isLoading } = useQuery({ queryKey: ['admin', 'transactions'], queryFn: adminApi.getPaymentTransactions });

  if (isLoading) return <LoadingSpinner />;
  if (transactions.length === 0) return <EmptyState title="Chưa có giao dịch nào" />;

  return (
    <div className="card overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
          <tr><th className="px-5 py-3">Loại</th><th className="px-5 py-3">Số tiền</th><th className="px-5 py-3">Mô tả</th><th className="px-5 py-3">Thời gian</th></tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {transactions.map((t) => (
            <tr key={t.id}>
              <td className="px-5 py-3"><Badge color="blue">{t.type}</Badge></td>
              <td className="px-5 py-3 font-medium">{formatCurrency(t.amount)}</td>
              <td className="px-5 py-3 text-slate-500">{t.description}</td>
              <td className="px-5 py-3">{formatDateTime(t.createdAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
