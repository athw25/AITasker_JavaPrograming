import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { ArrowLeft, Sparkles, Star } from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import { proposalsApi } from '../../api/proposals';
import { aiApi } from '../../api/ai';
import { errorMessage } from '../../api/client';
import { useAuthStore } from '../../store/authStore';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import Badge from '../../components/ui/Badge';
import EmptyState from '../../components/ui/EmptyState';
import Modal from '../../components/ui/Modal';
import { Textarea, Input } from '../../components/ui/Field';
import { statusBadge, JOB_STATUS, PROPOSAL_STATUS } from '../../utils/statusMaps';
import { formatCurrency, formatDate, formatDateTime } from '../../utils/format';

const TABS = ['overview', 'proposals', 'recommended'];

export default function JobDetail() {
  const { id } = useParams();
  const jobId = Number(id);
  const user = useAuthStore((s) => s.user);
  const isClient = user?.role === 'CLIENT';
  const queryClient = useQueryClient();
  const [tab, setTab] = useState('overview');
  const [applyOpen, setApplyOpen] = useState(false);
  const [applyForm, setApplyForm] = useState({ bidAmount: '', coverLetter: '', duration: '' });

  const { data: job, isLoading } = useQuery({ queryKey: ['job', jobId], queryFn: () => jobsApi.getById(jobId) });
  const { data: proposalsPage, isLoading: loadingProposals } = useQuery({
    queryKey: ['proposals', 'job', jobId],
    queryFn: () => proposalsApi.getByJob(jobId),
    enabled: tab === 'proposals'
  });
  const { data: recommended = [], isLoading: loadingRec } = useQuery({
    queryKey: ['recommend', jobId],
    queryFn: () => aiApi.recommendExperts(jobId),
    enabled: tab === 'recommended' && isClient
  });

  const acceptMutation = useMutation({
    mutationFn: proposalsApi.accept,
    onSuccess: () => { toast.success('Đã chấp nhận đề xuất!'); queryClient.invalidateQueries({ queryKey: ['proposals'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const rejectMutation = useMutation({
    mutationFn: proposalsApi.reject,
    onSuccess: () => { toast.success('Đã từ chối đề xuất'); queryClient.invalidateQueries({ queryKey: ['proposals'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const applyMutation = useMutation({
    mutationFn: () => proposalsApi.create({ jobId, ...applyForm, bidAmount: Number(applyForm.bidAmount), duration: Number(applyForm.duration) }),
    onSuccess: () => { toast.success('Đã gửi đề xuất!'); setApplyOpen(false); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;
  if (!job) return <EmptyState title="Không tìm thấy job" />;

  const s = statusBadge(JOB_STATUS, job.status);
  const proposals = proposalsPage?.content || [];
  const backPath = isClient ? '/client/jobs' : '/expert/browse-jobs';

  return (
    <div className="space-y-6">
      <Link to={backPath} className="inline-flex items-center gap-1 text-sm text-slate-500 hover:text-slate-700">
        <ArrowLeft size={16} /> Quay lại
      </Link>

      <div className="card p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-xl font-bold text-slate-900">{job.title}</h1>
            <p className="mt-1 text-sm text-slate-500">Đăng bởi {job.clientName}</p>
          </div>
          <Badge color={s.color}>{s.label}</Badge>
        </div>
        <div className="mt-4 flex flex-wrap gap-4 text-sm text-slate-600">
          <span>💰 {formatCurrency(job.budget)}</span>
          <span>📅 Hạn chót: {formatDate(job.deadline)}</span>
        </div>
        {!isClient && job.status === 'OPEN' && (
          <button className="btn-primary mt-4" onClick={() => setApplyOpen(true)}>Gửi đề xuất (Apply Proposal)</button>
        )}
      </div>

      <div className="flex gap-1 border-b border-slate-200">
        {TABS.filter((t) => t !== 'recommended' || isClient).map((t) => (
          <button key={t} onClick={() => setTab(t)}
            className={`border-b-2 px-4 py-2.5 text-sm font-medium capitalize ${tab === t ? 'border-primary-600 text-primary-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}>
            {t === 'overview' ? 'Tổng quan' : t === 'proposals' ? 'Đề xuất' : 'Chuyên gia đề xuất (AI)'}
          </button>
        ))}
      </div>

      {tab === 'overview' && (
        <div className="card p-6">
          <h3 className="mb-2 font-semibold text-slate-900">Mô tả công việc</h3>
          <p className="whitespace-pre-line text-sm text-slate-600">{job.description}</p>
          <h3 className="mb-2 mt-5 font-semibold text-slate-900">Kỹ năng yêu cầu</h3>
          <div className="flex flex-wrap gap-2">
            {(job.requiredSkills || '').split(',').map((sk) => sk.trim()).filter(Boolean).map((sk) => (
              <Badge key={sk} color="blue">{sk}</Badge>
            ))}
          </div>
        </div>
      )}

      {tab === 'proposals' && (
        loadingProposals ? <LoadingSpinner /> : proposals.length === 0 ? (
          <EmptyState title="Chưa có đề xuất nào" />
        ) : (
          <div className="space-y-3">
            {proposals.map((p) => {
              const ps = statusBadge(PROPOSAL_STATUS, p.status);
              return (
                <div key={p.id} className="card p-5">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="font-semibold text-slate-900">{p.expertName}</p>
                      <p className="text-sm text-slate-500">{formatCurrency(p.bidAmount)} · {formatDateTime(p.submittedAt)}</p>
                    </div>
                    <Badge color={ps.color}>{ps.label}</Badge>
                  </div>
                  <p className="mt-3 text-sm text-slate-600">{p.coverLetter}</p>
                  {isClient && p.status === 'PENDING' && (
                    <div className="mt-4 flex gap-2">
                      <button className="btn-success" onClick={() => acceptMutation.mutate(p.id)}>Chấp nhận</button>
                      <button className="btn-danger" onClick={() => rejectMutation.mutate(p.id)}>Từ chối</button>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )
      )}

      {tab === 'recommended' && (
        loadingRec ? <LoadingSpinner /> : recommended.length === 0 ? (
          <EmptyState title="Chưa có gợi ý phù hợp" />
        ) : (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            {recommended.map((r) => (
              <div key={r.expertId} className="card flex items-center justify-between p-5">
                <div>
                  <p className="font-semibold text-slate-900">{r.expertName}</p>
                  <p className="flex items-center gap-1 text-sm text-ai-600"><Sparkles size={14} /> Match Score: {r.matchScore}%</p>
                </div>
                <Star className="text-amber-400" size={20} />
              </div>
            ))}
          </div>
        )
      )}

      <Modal open={applyOpen} onClose={() => setApplyOpen(false)} title="Gửi đề xuất">
        <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); applyMutation.mutate(); }}>
          <Input label="Giá thầu (USD)" type="number" required value={applyForm.bidAmount}
            onChange={(e) => setApplyForm({ ...applyForm, bidAmount: e.target.value })} />
          <Input label="Thời gian hoàn thành (ngày)" type="number" required value={applyForm.duration}
            onChange={(e) => setApplyForm({ ...applyForm, duration: e.target.value })} />
          <Textarea label="Thư giới thiệu" required value={applyForm.coverLetter}
            onChange={(e) => setApplyForm({ ...applyForm, coverLetter: e.target.value })} />
          <div className="flex justify-end gap-3">
            <button type="button" className="btn-secondary" onClick={() => setApplyOpen(false)}>Hủy</button>
            <button className="btn-primary" disabled={applyMutation.isPending}>Gửi đề xuất</button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
