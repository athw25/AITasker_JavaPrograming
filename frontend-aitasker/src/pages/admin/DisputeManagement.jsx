import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { errorMessage } from '../../api/client';
import { disputeApi } from '../../api/dispute';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import Modal from '../../components/ui/Modal';
import { Textarea, Select } from '../../components/ui/Field';
import { statusBadge, DISPUTE_STATUS } from '../../utils/statusMaps';
import { formatDateTime } from '../../utils/format';

export default function DisputeManagement() {
  const queryClient = useQueryClient();
  const [status, setStatus] = useState('');
  const [resolving, setResolving] = useState(null);
  const [form, setForm] = useState({ status: 'RESOLVED', resolution: '', paymentId: '', refundAmount: '' });

  const { data: disputesPage, isLoading } = useQuery({ queryKey: ['admin', 'disputes', status], queryFn: () => disputeApi.getAll(status || undefined) });
  const disputes = disputesPage?.content ?? [];

  const resolveMutation = useMutation({
    mutationFn: () => disputeApi.resolve(resolving.id, {
      status: form.status,
      resolution: form.resolution,
      paymentId: form.paymentId ? Number(form.paymentId) : undefined,
      refundAmount: form.refundAmount ? Number(form.refundAmount) : undefined
    }),
    onSuccess: () => { toast.success('Đã xử lý tranh chấp'); setResolving(null); setForm({ status: 'RESOLVED', resolution: '', paymentId: '', refundAmount: '' }); queryClient.invalidateQueries({ queryKey: ['admin', 'disputes'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Dispute Management</h1>

      <select className="input w-56" value={status} onChange={(e) => setStatus(e.target.value)}>
        <option value="">Tất cả trạng thái</option>
        <option value="OPEN">Đang mở</option>
        <option value="IN_REVIEW">Đang xem xét</option>
        <option value="RESOLVED">Đã xử lý</option>
        <option value="REJECTED">Bị từ chối</option>
      </select>

      {isLoading ? <LoadingSpinner /> : disputes.length === 0 ? <EmptyState title="Không có tranh chấp nào" /> : (
        <div className="space-y-3">
          {disputes.map((d) => {
            const s = statusBadge(DISPUTE_STATUS, d.status);
            const resolved = d.status === 'RESOLVED' || d.status === 'REJECTED';
            return (
              <div key={d.id} className="card p-5">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="font-semibold text-slate-900">Dispute #{d.id} — Project #{d.projectId}</p>
                    <p className="text-sm text-slate-500">Người tạo: {d.createdByName} · {formatDateTime(d.createdAt)}</p>
                  </div>
                  <Badge color={s.color}>{s.label}</Badge>
                </div>
                <p className="mt-3 text-sm text-slate-600"><strong>Lý do:</strong> {d.reason}</p>
                {d.resolution && <p className="mt-1 text-sm text-slate-600"><strong>Kết quả:</strong> {d.resolution}</p>}
                {!resolved && (
                  <button className="btn-primary mt-4" onClick={() => setResolving(d)}>Xử lý tranh chấp</button>
                )}
              </div>
            );
          })}
        </div>
      )}

      <Modal open={!!resolving} onClose={() => setResolving(null)} title={`Xử lý Dispute #${resolving?.id}`}>
        <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); resolveMutation.mutate(); }}>
          <Select label="Quyết định" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
            <option value="RESOLVED">Chấp nhận xử lý (có thể kèm hoàn tiền)</option>
            <option value="REJECTED">Từ chối tranh chấp</option>
          </Select>
          {form.status === 'RESOLVED' && (
            <div className="grid grid-cols-2 gap-3">
              <input className="input" type="number" placeholder="Payment ID cần hoàn tiền (bỏ trống nếu không hoàn tiền)"
                value={form.paymentId} onChange={(e) => setForm({ ...form, paymentId: e.target.value })} />
              <input className="input" type="number" placeholder="Số tiền hoàn (USD)"
                value={form.refundAmount} onChange={(e) => setForm({ ...form, refundAmount: e.target.value })} />
            </div>
          )}
          <Textarea label="Lý do / kết luận" required value={form.resolution} onChange={(e) => setForm({ ...form, resolution: e.target.value })} />
          <button className="btn-primary w-full" disabled={resolveMutation.isPending}>Xác nhận xử lý</button>
        </form>
      </Modal>
    </div>
  );
}
