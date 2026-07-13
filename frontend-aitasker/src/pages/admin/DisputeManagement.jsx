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
  const [form, setForm] = useState({ status: 'RESOLVED_REFUND', resolution: '' });

  const { data: disputes = [], isLoading } = useQuery({ queryKey: ['admin', 'disputes', status], queryFn: () => disputeApi.getAll(status || undefined) });

  const resolveMutation = useMutation({
    mutationFn: () => disputeApi.resolve(resolving.id, form),
    onSuccess: () => { toast.success('Đã xử lý tranh chấp'); setResolving(null); setForm({ status: 'RESOLVED_REFUND', resolution: '' }); queryClient.invalidateQueries({ queryKey: ['admin', 'disputes'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Dispute Management</h1>

      <select className="input w-56" value={status} onChange={(e) => setStatus(e.target.value)}>
        <option value="">Tất cả trạng thái</option>
        <option value="OPEN">Đang mở</option>
        <option value="UNDER_REVIEW">Đang xem xét</option>
        <option value="RESOLVED_REFUND">Đã hoàn tiền</option>
        <option value="RESOLVED_REJECTED">Bị từ chối</option>
      </select>

      {isLoading ? <LoadingSpinner /> : disputes.length === 0 ? <EmptyState title="Không có tranh chấp nào" /> : (
        <div className="space-y-3">
          {disputes.map((d) => {
            const s = statusBadge(DISPUTE_STATUS, d.status);
            const resolved = d.status === 'RESOLVED_REFUND' || d.status === 'RESOLVED_REJECTED';
            return (
              <div key={d.id} className="card p-5">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="font-semibold text-slate-900">Dispute #{d.id} — Project #{d.projectId}</p>
                    <p className="text-sm text-slate-500">Người tạo: {d.creatorName} · {formatDateTime(d.createdAt)}</p>
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
            <option value="RESOLVED_REFUND">Hoàn tiền cho Client</option>
            <option value="RESOLVED_REJECTED">Từ chối tranh chấp</option>
          </Select>
          <Textarea label="Lý do / kết luận" required value={form.resolution} onChange={(e) => setForm({ ...form, resolution: e.target.value })} />
          <button className="btn-primary w-full" disabled={resolveMutation.isPending}>Xác nhận xử lý</button>
        </form>
      </Modal>
    </div>
  );
}
