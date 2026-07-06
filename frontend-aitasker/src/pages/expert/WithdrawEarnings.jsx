import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Wallet } from 'lucide-react';
import { withdrawalsApi } from '../../api/payments';
import { errorMessage } from '../../api/client';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import Modal from '../../components/ui/Modal';
import StatCard from '../../components/ui/StatCard';
import { statusBadge, WITHDRAWAL_STATUS } from '../../utils/statusMaps';
import { formatCurrency, formatDateTime } from '../../utils/format';

export default function WithdrawEarnings() {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [amount, setAmount] = useState('');

  const { data: withdrawals = [], isLoading } = useQuery({ queryKey: ['withdrawals', 'me'], queryFn: withdrawalsApi.getMine });

  const requestMutation = useMutation({
    mutationFn: () => withdrawalsApi.request({ amount: Number(amount) }),
    onSuccess: () => { toast.success('Đã gửi yêu cầu rút tiền'); setOpen(false); setAmount(''); queryClient.invalidateQueries({ queryKey: ['withdrawals'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  const pending = withdrawals.filter((w) => w.status === 'PENDING').reduce((s, w) => s + Number(w.amount), 0);
  const approved = withdrawals.filter((w) => w.status === 'APPROVED').reduce((s, w) => s + Number(w.amount), 0);

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Withdraw & Earnings</h1>
        <button className="btn-primary" onClick={() => setOpen(true)}><Wallet size={16} /> Yêu cầu rút tiền</button>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <StatCard icon={Wallet} label="Đang chờ duyệt" value={formatCurrency(pending)} color="orange" />
        <StatCard icon={Wallet} label="Đã duyệt" value={formatCurrency(approved)} color="emerald" />
      </div>

      {withdrawals.length === 0 ? <EmptyState title="Chưa có yêu cầu rút tiền nào" /> : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr><th className="px-5 py-3">Số tiền</th><th className="px-5 py-3">Ngày yêu cầu</th><th className="px-5 py-3">Trạng thái</th></tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {withdrawals.map((w) => {
                const s = statusBadge(WITHDRAWAL_STATUS, w.status);
                return (
                  <tr key={w.id}>
                    <td className="px-5 py-3 font-medium">{formatCurrency(w.amount)}</td>
                    <td className="px-5 py-3">{formatDateTime(w.requestedAt)}</td>
                    <td className="px-5 py-3"><Badge color={s.color}>{s.label}</Badge></td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={open} onClose={() => setOpen(false)} title="Yêu cầu rút tiền" size="sm">
        <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); requestMutation.mutate(); }}>
          <input className="input" type="number" placeholder="Số tiền (USD)" required value={amount} onChange={(e) => setAmount(e.target.value)} />
          <button className="btn-primary w-full" disabled={requestMutation.isPending}>Gửi yêu cầu</button>
        </form>
      </Modal>
    </div>
  );
}
