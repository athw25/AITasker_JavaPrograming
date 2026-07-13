import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Send, Upload, CheckCircle2, XCircle, DollarSign, Star, ShieldAlert } from 'lucide-react';
import { disputeApi } from '../../api/dispute';
import { projectsApi } from '../../api/projects';
import { milestonesApi } from '../../api/milestones';
import { deliveriesApi } from '../../api/deliveries';
import { paymentsApi } from '../../api/payments';
import { messagesApi } from '../../api/messages';
import { reviewsApi } from '../../api/reviews';
import { errorMessage } from '../../api/client';
import { useAuthStore } from '../../store/authStore';
import { useChatSocket } from '../../hooks/useChatSocket';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import Badge from '../../components/ui/Badge';
import Modal from '../../components/ui/Modal';
import Drawer from '../../components/ui/Drawer';
import EmptyState from '../../components/ui/EmptyState';
import { Input, Textarea } from '../../components/ui/Field';
import { statusBadge, PROJECT_STATUS, MILESTONE_STATUS, DELIVERY_STATUS, DISPUTE_STATUS } from '../../utils/statusMaps';
import { formatCurrency, formatDate, formatDateTime } from '../../utils/format';

const TABS = ['overview', 'milestones', 'escrow', 'chat', 'reviews'];

export default function ProjectWorkspace() {
  const { id } = useParams();
  const projectId = Number(id);
  const user = useAuthStore((s) => s.user);
  const isClient = user?.role === 'CLIENT';
  const queryClient = useQueryClient();
  const [tab, setTab] = useState('overview');
  const [disputeOpen, setDisputeOpen] = useState(false);
  const [disputeReason, setDisputeReason] = useState('');
  const [disputeDetailOpen, setDisputeDetailOpen] = useState(false);
  const [disputeMessage, setDisputeMessage] = useState('');

  const { data: project, isLoading } = useQuery({ queryKey: ['project', projectId], queryFn: () => projectsApi.getById(projectId) });

  const { data: disputesPage } = useQuery({ queryKey: ['disputes', 'mine'], queryFn: () => disputeApi.getAll(undefined, 0, 100) });
  const existingDispute = (disputesPage?.content ?? []).find((d) => d.projectId === projectId);

  const disputeMutation = useMutation({
    mutationFn: () => disputeApi.create({ projectId, reason: disputeReason }),
    onSuccess: () => { toast.success('Đã gửi yêu cầu tranh chấp'); setDisputeOpen(false); setDisputeReason(''); queryClient.invalidateQueries({ queryKey: ['project', projectId] }); queryClient.invalidateQueries({ queryKey: ['disputes', 'mine'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  const disputeMessageMutation = useMutation({
    mutationFn: () => disputeApi.addMessage(existingDispute.id, disputeMessage),
    onSuccess: () => { setDisputeMessage(''); queryClient.invalidateQueries({ queryKey: ['disputes', 'mine'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;
  if (!project) return <EmptyState title="Không tìm thấy dự án" />;

  const s = statusBadge(PROJECT_STATUS, project.status);

  return (
    <div className="space-y-6">
      <div className="card p-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-xl font-bold text-slate-900">{project.jobTitle}</h1>
            <p className="mt-1 text-sm text-slate-500">
              Client: {project.clientName} · Expert: {project.expertName}
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Badge color={s.color}>{s.label}</Badge>
            {existingDispute ? (
              <button className="btn-secondary !border-red-200 !text-red-600" onClick={() => setDisputeDetailOpen(true)}>
                <ShieldAlert size={14} /> Xem tranh chấp
              </button>
            ) : project.status === 'ACTIVE' && (
              <button className="btn-secondary !border-red-200 !text-red-600" onClick={() => setDisputeOpen(true)}>
                <ShieldAlert size={14} /> Báo cáo tranh chấp
              </button>
            )}
          </div>
        </div>
        <div className="mt-4 grid grid-cols-2 gap-4 text-sm text-slate-600 sm:grid-cols-4">
          <div><p className="text-xs text-slate-400">Bắt đầu</p>{formatDate(project.startDate)}</div>
          <div><p className="text-xs text-slate-400">Kết thúc dự kiến</p>{formatDate(project.endDate)}</div>
          <div><p className="text-xs text-slate-400">Tổng ngân sách</p>{formatCurrency(project.totalBudget)}</div>
          <div><p className="text-xs text-slate-400">Milestone hoàn thành</p>{project.completedMilestones}/{project.totalMilestones}</div>
        </div>
      </div>

      <div className="flex gap-1 overflow-x-auto border-b border-slate-200">
        {TABS.map((t) => (
          <button key={t} onClick={() => setTab(t)}
            className={`shrink-0 border-b-2 px-4 py-2.5 text-sm font-medium capitalize ${tab === t ? 'border-primary-600 text-primary-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}>
            {{ overview: 'Tổng quan', milestones: 'Milestones', escrow: 'Escrow', chat: 'Chat', reviews: 'Đánh giá' }[t]}
          </button>
        ))}
      </div>

      {tab === 'overview' && <OverviewTab project={project} />}
      {tab === 'milestones' && <MilestonesTab projectId={projectId} isClient={isClient} />}
      {tab === 'escrow' && <EscrowTab projectId={projectId} isClient={isClient} />}
      {tab === 'chat' && <ChatTab projectId={projectId} project={project} user={user} />}
      {tab === 'reviews' && <ReviewsTab project={project} user={user} queryClient={queryClient} />}

      <Modal open={disputeOpen} onClose={() => setDisputeOpen(false)} title="Báo cáo tranh chấp" size="sm">
        <Textarea label="Lý do tranh chấp" required value={disputeReason} onChange={(e) => setDisputeReason(e.target.value)} />
        <div className="mt-4 flex justify-end gap-3">
          <button className="btn-secondary" onClick={() => setDisputeOpen(false)}>Hủy</button>
          <button className="btn-danger" disabled={!disputeReason || disputeMutation.isPending} onClick={() => disputeMutation.mutate()}>
            Gửi tranh chấp
          </button>
        </div>
      </Modal>

      {existingDispute && (
        <Modal open={disputeDetailOpen} onClose={() => setDisputeDetailOpen(false)} title={`Tranh chấp #${existingDispute.id}`}>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <Badge color={statusBadge(DISPUTE_STATUS, existingDispute.status).color}>
                {statusBadge(DISPUTE_STATUS, existingDispute.status).label}
              </Badge>
              <span className="text-xs text-slate-400">{formatDateTime(existingDispute.createdAt)}</span>
            </div>
            <p className="text-sm text-slate-600"><strong>Lý do:</strong> {existingDispute.reason}</p>
            {existingDispute.resolution && (
              <p className="text-sm text-slate-600"><strong>Kết quả:</strong> {existingDispute.resolution}</p>
            )}

            <div className="max-h-64 space-y-2 overflow-y-auto border-t border-slate-100 pt-3">
              {(existingDispute.messages ?? []).length === 0 && (
                <p className="text-sm text-slate-400">Chưa có tin nhắn nào.</p>
              )}
              {(existingDispute.messages ?? []).map((m) => (
                <div key={m.id} className="rounded-lg bg-slate-50 px-3 py-2">
                  <p className="text-xs font-medium text-slate-700">{m.senderName}</p>
                  <p className="text-sm text-slate-600">{m.message}</p>
                  <p className="text-[10px] text-slate-400">{formatDateTime(m.createdAt)}</p>
                </div>
              ))}
            </div>

            {existingDispute.status !== 'RESOLVED' && existingDispute.status !== 'REJECTED' && (
              <form className="flex gap-2" onSubmit={(e) => { e.preventDefault(); disputeMessageMutation.mutate(); }}>
                <input className="input" placeholder="Nhập tin nhắn..." value={disputeMessage}
                  onChange={(e) => setDisputeMessage(e.target.value)} />
                <button className="btn-primary shrink-0" disabled={!disputeMessage.trim() || disputeMessageMutation.isPending}>
                  <Send size={16} />
                </button>
              </form>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
}

function OverviewTab({ project }) {
  return (
    <div className="card p-6">
      <h3 className="mb-4 font-semibold text-slate-900">Tiến độ dự án</h3>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-xl bg-slate-50 p-4">
          <p className="text-xs text-slate-400">Đã thanh toán</p>
          <p className="text-lg font-bold text-emerald-600">{formatCurrency(project.paidAmount)}</p>
        </div>
        <div className="rounded-xl bg-slate-50 p-4">
          <p className="text-xs text-slate-400">Còn lại</p>
          <p className="text-lg font-bold text-slate-700">{formatCurrency(project.remainingAmount)}</p>
        </div>
        <div className="rounded-xl bg-slate-50 p-4">
          <p className="text-xs text-slate-400">Job liên quan</p>
          <p className="text-lg font-bold text-slate-700">#{project.jobId}</p>
        </div>
      </div>
    </div>
  );
}

function MilestonesTab({ projectId, isClient }) {
  const queryClient = useQueryClient();
  const [createOpen, setCreateOpen] = useState(false);
  const [form, setForm] = useState({ title: '', description: '', amount: '', dueDate: '' });
  const [deliveryDrawer, setDeliveryDrawer] = useState(null);
  const [rejectTarget, setRejectTarget] = useState(null);
  const [rejectReason, setRejectReason] = useState('');

  const { data: milestones = [], isLoading } = useQuery({
    queryKey: ['milestones', projectId],
    queryFn: () => milestonesApi.getByProject(projectId)
  });

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['milestones', projectId] });

  const createMutation = useMutation({
    mutationFn: () => milestonesApi.create({ projectId, ...form, amount: Number(form.amount) }),
    onSuccess: () => { toast.success('Đã tạo Milestone'); setCreateOpen(false); setForm({ title: '', description: '', amount: '', dueDate: '' }); invalidate(); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const approveMutation = useMutation({
    mutationFn: milestonesApi.approve,
    onSuccess: () => { toast.success('Đã duyệt Milestone'); invalidate(); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const rejectMutation = useMutation({
    mutationFn: ({ id, reason }) => milestonesApi.reject(id, reason),
    onSuccess: () => { toast.success('Đã từ chối bản giao'); setRejectTarget(null); setRejectReason(''); invalidate(); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const releaseMutation = useMutation({
    mutationFn: milestonesApi.releasePayment,
    onSuccess: () => { toast.success('Đã giải ngân thanh toán'); invalidate(); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-4">
      {isClient && (
        <div className="flex justify-end">
          <button className="btn-primary" onClick={() => setCreateOpen(true)}>+ Tạo Milestone</button>
        </div>
      )}

      {milestones.length === 0 ? <EmptyState title="Chưa có Milestone nào" /> : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr>
                <th className="px-5 py-3">Tiêu đề</th>
                <th className="px-5 py-3">Số tiền</th>
                <th className="px-5 py-3">Hạn</th>
                <th className="px-5 py-3">Trạng thái</th>
                <th className="px-5 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {milestones.map((m) => {
                const s = statusBadge(MILESTONE_STATUS, m.status);
                return (
                  <tr key={m.id} className="hover:bg-slate-50">
                    <td className="px-5 py-3">
                      <button className="font-medium text-primary-700 hover:underline" onClick={() => setDeliveryDrawer(m)}>{m.title}</button>
                    </td>
                    <td className="px-5 py-3">{formatCurrency(m.amount)}</td>
                    <td className="px-5 py-3">{formatDate(m.dueDate)}</td>
                    <td className="px-5 py-3"><Badge color={s.color}>{s.label}</Badge></td>
                    <td className="px-5 py-3">
                      <div className="flex justify-end gap-2">
                        {!isClient && (m.status === 'PENDING' || m.status === 'REJECTED') && (
                          <button className="btn-secondary !px-3" onClick={() => setDeliveryDrawer(m)}><Upload size={14} /> Nộp bản giao</button>
                        )}
                        {isClient && m.status === 'SUBMITTED' && (
                          <>
                            <button className="btn-success !px-3" onClick={() => approveMutation.mutate(m.id)}><CheckCircle2 size={14} /> Duyệt</button>
                            <button className="btn-danger !px-3" onClick={() => setRejectTarget(m)}><XCircle size={14} /> Từ chối</button>
                          </>
                        )}
                        {isClient && m.status === 'APPROVED' && (
                          <button className="btn-primary !px-3" onClick={() => releaseMutation.mutate(m.id)}><DollarSign size={14} /> Giải ngân</button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={createOpen} onClose={() => setCreateOpen(false)} title="Tạo Milestone mới">
        <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); createMutation.mutate(); }}>
          <Input label="Tiêu đề" required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <Textarea label="Mô tả" required value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Số tiền (USD)" type="number" required value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} />
            <Input label="Hạn hoàn thành" type="date" required value={form.dueDate} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} />
          </div>
          <div className="flex justify-end gap-3">
            <button type="button" className="btn-secondary" onClick={() => setCreateOpen(false)}>Hủy</button>
            <button className="btn-primary" disabled={createMutation.isPending}>Tạo</button>
          </div>
        </form>
      </Modal>

      <Modal open={!!rejectTarget} onClose={() => setRejectTarget(null)} title="Từ chối bản giao" size="sm">
        <Textarea label="Lý do từ chối" required value={rejectReason} onChange={(e) => setRejectReason(e.target.value)} />
        <div className="mt-4 flex justify-end gap-3">
          <button className="btn-secondary" onClick={() => setRejectTarget(null)}>Hủy</button>
          <button className="btn-danger" disabled={!rejectReason || rejectMutation.isPending}
            onClick={() => rejectMutation.mutate({ id: rejectTarget.id, reason: rejectReason })}>Từ chối</button>
        </div>
      </Modal>

      {deliveryDrawer && (
        <DeliveryDrawer milestone={deliveryDrawer} isClient={isClient} onClose={() => setDeliveryDrawer(null)} onSubmitted={invalidate} />
      )}
    </div>
  );
}

function DeliveryDrawer({ milestone, isClient, onClose, onSubmitted }) {
  const [form, setForm] = useState({ fileUrl: '', note: '' });
  const { data: deliveries = [], isLoading } = useQuery({
    queryKey: ['deliveries', milestone.id],
    queryFn: () => deliveriesApi.getByMilestone(milestone.id)
  });

  const submitMutation = useMutation({
    mutationFn: () => milestonesApi.submit(milestone.id, form),
    onSuccess: () => { toast.success('Đã nộp bản giao'); onSubmitted(); onClose(); },
    onError: (e) => toast.error(errorMessage(e))
  });

  return (
    <Drawer open onClose={onClose} title={`Bản giao — ${milestone.title}`}>
      <div className="space-y-4">
        {isLoading ? <LoadingSpinner /> : deliveries.length === 0 ? (
          <p className="text-sm text-slate-400">Chưa có bản giao nào.</p>
        ) : (
          <div className="space-y-3">
            {deliveries.map((d) => {
              const s = statusBadge(DELIVERY_STATUS, d.status);
              return (
                <div key={d.id} className="rounded-lg border border-slate-200 p-3">
                  <div className="flex items-center justify-between">
                    <a href={d.fileUrl} target="_blank" rel="noreferrer" className="text-sm font-medium text-primary-600 hover:underline">
                      {d.fileUrl}
                    </a>
                    <Badge color={s.color}>{s.label}</Badge>
                  </div>
                  <p className="mt-1 text-xs text-slate-500">{d.note}</p>
                  <p className="mt-1 text-[11px] text-slate-400">{formatDateTime(d.createdAt)}</p>
                </div>
              );
            })}
          </div>
        )}

        {!isClient && (milestone.status === 'PENDING' || milestone.status === 'REJECTED') && (
          <form className="space-y-3 border-t border-slate-100 pt-4" onSubmit={(e) => { e.preventDefault(); submitMutation.mutate(); }}>
            <Input label="Link file (Google Drive, Github...)" required value={form.fileUrl}
              onChange={(e) => setForm({ ...form, fileUrl: e.target.value })} />
            <Textarea label="Ghi chú" value={form.note} onChange={(e) => setForm({ ...form, note: e.target.value })} />
            <button className="btn-primary w-full" disabled={submitMutation.isPending}>
              <Send size={14} /> Nộp bản giao
            </button>
          </form>
        )}
      </div>
    </Drawer>
  );
}

function EscrowTab({ projectId, isClient }) {
  const queryClient = useQueryClient();
  const [form, setForm] = useState({ amount: '' });

  const { data: milestones = [] } = useQuery({
    queryKey: ['milestones', projectId],
    queryFn: () => milestonesApi.getByProject(projectId)
  });

  const depositMutation = useMutation({
    mutationFn: () => paymentsApi.deposit({ projectId, amount: Number(form.amount) }),
    onSuccess: () => { toast.success('Đã nạp tiền vào Escrow'); setForm({ amount: '' }); queryClient.invalidateQueries({ queryKey: ['milestones', projectId] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  return (
    <div className="space-y-6">
      {isClient && (
        <div className="card p-5">
          <h3 className="mb-3 font-semibold text-slate-900">Nạp tiền vào Escrow</h3>
          <form className="flex gap-3" onSubmit={(e) => { e.preventDefault(); depositMutation.mutate(); }}>
            <input className="input" type="number" placeholder="Số tiền (USD)" required
              value={form.amount} onChange={(e) => setForm({ amount: e.target.value })} />
            <button className="btn-primary shrink-0" disabled={depositMutation.isPending}>Nạp tiền</button>
          </form>
        </div>
      )}

      <div className="card p-5">
        <h3 className="mb-4 font-semibold text-slate-900">Trạng thái thanh toán theo Milestone</h3>
        {milestones.length === 0 ? <p className="text-sm text-slate-400">Chưa có milestone nào.</p> : (
          <div className="space-y-3">
            {milestones.map((m) => {
              const s = statusBadge(MILESTONE_STATUS, m.status);
              return (
                <div key={m.id} className="flex items-center justify-between rounded-lg bg-slate-50 px-4 py-3">
                  <div>
                    <p className="text-sm font-medium text-slate-800">{m.title}</p>
                    <p className="text-xs text-slate-500">{formatCurrency(m.amount)}</p>
                  </div>
                  <Badge color={s.color}>{s.label}</Badge>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}

function ChatTab({ projectId, project, user }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const receiverId = user?.role === 'CLIENT' ? project.expertId : project.clientId;

  const { data: history = [] } = useQuery({ queryKey: ['messages', projectId], queryFn: () => messagesApi.getByProject(projectId) });
  useEffect(() => { setMessages(history); }, [history]);

  const { connected, sendMessage } = useChatSocket(projectId, (msg) => {
    setMessages((prev) => [...prev, msg]);
  });

  const submit = (e) => {
    e.preventDefault();
    if (!input.trim()) return;
    sendMessage(receiverId, input.trim());
    setInput('');
  };

  return (
    <div className="card flex h-[520px] flex-col p-5">
      <div className="mb-3 flex items-center justify-between">
        <h3 className="font-semibold text-slate-900">Chat dự án</h3>
        <span className={`text-xs ${connected ? 'text-emerald-600' : 'text-slate-400'}`}>{connected ? '● Đang kết nối' : '○ Mất kết nối'}</span>
      </div>
      <div className="flex-1 space-y-3 overflow-y-auto">
        {messages.length === 0 && <p className="text-center text-sm text-slate-400">Chưa có tin nhắn nào.</p>}
        {messages.map((m, i) => {
          const mine = m.senderId === user?.id;
          return (
            <div key={m.id || i} className={`flex ${mine ? 'justify-end' : 'justify-start'}`}>
              <div className={`max-w-xs rounded-2xl px-4 py-2 text-sm ${mine ? 'bg-primary-600 text-white' : 'bg-slate-100 text-slate-800'}`}>
                {m.content}
                <p className={`mt-1 text-[10px] ${mine ? 'text-primary-100' : 'text-slate-400'}`}>{formatDateTime(m.sentAt)}</p>
              </div>
            </div>
          );
        })}
      </div>
      <form className="mt-3 flex gap-2" onSubmit={submit}>
        <input className="input" placeholder="Nhập tin nhắn..." value={input} onChange={(e) => setInput(e.target.value)} />
        <button className="btn-primary shrink-0"><Send size={16} /></button>
      </form>
    </div>
  );
}

function ReviewsTab({ project, user, queryClient }) {
  const [form, setForm] = useState({ rating: 5, comment: '' });
  const revieweeId = user?.role === 'CLIENT' ? project.expertId : project.clientId;

  const { data: reviews = [], isLoading } = useQuery({
    queryKey: ['reviews', revieweeId],
    queryFn: () => reviewsApi.getByUser(revieweeId),
    enabled: !!revieweeId
  });

  const submitMutation = useMutation({
    mutationFn: () => reviewsApi.create({
      revieweeId,
      projectId: project.id,
      rating: Number(form.rating),
      comment: form.comment,
      type: user.role === 'CLIENT' ? 'CLIENT_TO_EXPERT' : 'EXPERT_TO_cLIENT'
    }),
    onSuccess: () => { toast.success('Đã gửi đánh giá'); queryClient.invalidateQueries({ queryKey: ['reviews', revieweeId] }); setForm({ rating: 5, comment: '' }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      {project.status === 'COMPLETED' && (
        <div className="card p-5">
          <h3 className="mb-3 font-semibold text-slate-900">Gửi đánh giá</h3>
          <form className="space-y-3" onSubmit={(e) => { e.preventDefault(); submitMutation.mutate(); }}>
            <div className="flex gap-1">
              {[1, 2, 3, 4, 5].map((n) => (
                <button type="button" key={n} onClick={() => setForm({ ...form, rating: n })}>
                  <Star size={24} className={n <= form.rating ? 'fill-amber-400 text-amber-400' : 'text-slate-300'} />
                </button>
              ))}
            </div>
            <Textarea placeholder="Nhận xét của bạn..." value={form.comment} onChange={(e) => setForm({ ...form, comment: e.target.value })} />
            <button className="btn-primary" disabled={submitMutation.isPending}>Gửi đánh giá</button>
          </form>
        </div>
      )}

      <div className="card p-5">
        <h3 className="mb-4 font-semibold text-slate-900">Đánh giá</h3>
        {reviews.length === 0 ? <p className="text-sm text-slate-400">Chưa có đánh giá nào.</p> : (
          <div className="space-y-4">
            {reviews.map((r) => (
              <div key={r.id} className="border-b border-slate-100 pb-3 last:border-0">
                <div className="flex gap-0.5">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star key={i} size={14} className={i < r.rating ? 'fill-amber-400 text-amber-400' : 'text-slate-300'} />
                  ))}
                </div>
                <p className="mt-1 text-sm text-slate-600">{r.comment}</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
