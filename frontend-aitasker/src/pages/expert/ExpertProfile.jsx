import { useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Sparkles, Plus, Trash2, Star, ExternalLink } from 'lucide-react';
import { expertsApi, portfolioApi, servicePackagesApi } from '../../api/experts';
import { reviewsApi } from '../../api/reviews';
import { aiApi } from '../../api/ai';
import { errorMessage } from '../../api/client';
import { useAuthStore } from '../../store/authStore';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { Input, Textarea } from '../../components/ui/Field';
import { formatCurrency } from '../../utils/format';

const TABS = ['info', 'portfolio', 'services', 'reviews'];

export default function ExpertProfile() {
  const [tab, setTab] = useState('info');
  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Expert Profile</h1>
      <div className="flex gap-1 border-b border-slate-200">
        {TABS.map((t) => (
          <button key={t} onClick={() => setTab(t)}
            className={`border-b-2 px-4 py-2.5 text-sm font-medium capitalize ${tab === t ? 'border-primary-600 text-primary-600' : 'border-transparent text-slate-500 hover:text-slate-700'}`}>
            {{ info: 'Thông tin', portfolio: 'Portfolio', services: 'Dịch vụ', reviews: 'Đánh giá' }[t]}
          </button>
        ))}
      </div>
      {tab === 'info' && <InfoTab />}
      {tab === 'portfolio' && <PortfolioTab />}
      {tab === 'services' && <ServicesTab />}
      {tab === 'reviews' && <ReviewsTab />}
    </div>
  );
}

function InfoTab() {
  const { data: profile, isLoading } = useQuery({ queryKey: ['expertProfile'], queryFn: expertsApi.getMyProfile });
  const [form, setForm] = useState({ fullName: '', title: '', skills: '', experienceYears: '', hourlyRate: '' });
  const queryClient = useQueryClient();

  useEffect(() => {
    if (profile) setForm({
      fullName: profile.fullName || '', title: profile.title || '', skills: profile.skills || '',
      experienceYears: profile.experienceYears ?? '', hourlyRate: profile.hourlyRate ?? ''
    });
  }, [profile]);

  const updateMutation = useMutation({
    mutationFn: () => expertsApi.updateMyProfile({ ...form, experienceYears: Number(form.experienceYears), hourlyRate: Number(form.hourlyRate) }),
    onSuccess: () => { toast.success('Đã cập nhật hồ sơ'); queryClient.invalidateQueries({ queryKey: ['expertProfile'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="card max-w-2xl p-6">
      <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); updateMutation.mutate(); }}>
        <Input label="Họ và tên" required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
        <Input label="Vị trí chuyên môn" required placeholder="VD: Senior AI Engineer" value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })} />
        <Input label="Kỹ năng (phân tách bởi dấu phẩy)" required value={form.skills} onChange={(e) => setForm({ ...form, skills: e.target.value })} />
        <div className="grid grid-cols-2 gap-4">
          <Input label="Số năm kinh nghiệm" type="number" required value={form.experienceYears}
            onChange={(e) => setForm({ ...form, experienceYears: e.target.value })} />
          <Input label="Giá theo giờ (USD)" type="number" required value={form.hourlyRate}
            onChange={(e) => setForm({ ...form, hourlyRate: e.target.value })} />
        </div>
        <button className="btn-primary" disabled={updateMutation.isPending}>Lưu thay đổi</button>
      </form>
    </div>
  );
}

function PortfolioTab() {
  const user = useAuthStore((s) => s.user);
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({ projectName: '', description: '', projectUrl: '' });
  const [deleteTarget, setDeleteTarget] = useState(null);

  const { data: items = [], isLoading } = useQuery({
    queryKey: ['portfolio', user?.id],
    queryFn: () => portfolioApi.getByExpert(user.id),
    enabled: !!user?.id
  });

  const addMutation = useMutation({
    mutationFn: () => portfolioApi.add(form),
    onSuccess: () => { toast.success('Đã thêm portfolio'); setOpen(false); setForm({ projectName: '', description: '', projectUrl: '' }); queryClient.invalidateQueries({ queryKey: ['portfolio'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const removeMutation = useMutation({
    mutationFn: portfolioApi.remove,
    onSuccess: () => { toast.success('Đã xóa'); setDeleteTarget(null); queryClient.invalidateQueries({ queryKey: ['portfolio'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-4">
      <div className="flex justify-end"><button className="btn-primary" onClick={() => setOpen(true)}><Plus size={16} /> Thêm dự án</button></div>
      {items.length === 0 ? <EmptyState title="Chưa có portfolio nào" /> : (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {items.map((p) => (
            <div key={p.id} className="card p-5">
              <div className="flex items-start justify-between">
                <p className="font-semibold text-slate-900">{p.projectName}</p>
                <button className="text-slate-400 hover:text-red-500" onClick={() => setDeleteTarget(p)}><Trash2 size={16} /></button>
              </div>
              <p className="mt-2 text-sm text-slate-500">{p.description}</p>
              {p.projectUrl && (
                <a href={p.projectUrl} target="_blank" rel="noreferrer" className="mt-2 inline-flex items-center gap-1 text-sm text-primary-600 hover:underline">
                  Xem dự án <ExternalLink size={12} />
                </a>
              )}
            </div>
          ))}
        </div>
      )}
      <Modal open={open} onClose={() => setOpen(false)} title="Thêm dự án Portfolio">
        <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); addMutation.mutate(); }}>
          <Input label="Tên dự án" required value={form.projectName} onChange={(e) => setForm({ ...form, projectName: e.target.value })} />
          <Textarea label="Mô tả" required value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <Input label="Link dự án" value={form.projectUrl} onChange={(e) => setForm({ ...form, projectUrl: e.target.value })} />
          <button className="btn-primary w-full" disabled={addMutation.isPending}>Thêm</button>
        </form>
      </Modal>
      <ConfirmDialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} danger title="Xóa Portfolio"
        message={`Xóa dự án "${deleteTarget?.projectName}"?`} loading={removeMutation.isPending}
        onConfirm={() => removeMutation.mutate(deleteTarget.id)} />
    </div>
  );
}

function ServicesTab() {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ packageName: '', price: '', deliveryDays: '' });
  const [aiPrompt, setAiPrompt] = useState('');
  const [deleteTarget, setDeleteTarget] = useState(null);

  const { data: services = [], isLoading } = useQuery({ queryKey: ['services'], queryFn: servicePackagesApi.getAll });

  const createMutation = useMutation({
    mutationFn: () => servicePackagesApi.create({ ...form, price: Number(form.price), deliveryDays: Number(form.deliveryDays) }),
    onSuccess: () => { toast.success('Đã đăng dịch vụ'); closeModal(); queryClient.invalidateQueries({ queryKey: ['services'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const updateMutation = useMutation({
    mutationFn: ({ id, payload }) => servicePackagesApi.update(id, payload),
    onSuccess: () => { toast.success('Đã cập nhật'); closeModal(); queryClient.invalidateQueries({ queryKey: ['services'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const removeMutation = useMutation({
    mutationFn: servicePackagesApi.remove,
    onSuccess: () => { toast.success('Đã xóa dịch vụ'); setDeleteTarget(null); queryClient.invalidateQueries({ queryKey: ['services'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const aiMutation = useMutation({
    mutationFn: () => aiApi.serviceGenerator(aiPrompt),
    onSuccess: (data) => {
      setForm({
        packageName: data.title || '',
        price: data.suggestedPrice ?? '',
        deliveryDays: data.deliveryDays || form.deliveryDays || 7
      });
      toast.success('AI đã sinh nội dung dịch vụ!');
    },
    onError: (e) => toast.error(errorMessage(e))
  });

  const openCreate = () => { setEditing(null); setForm({ packageName: '', price: '', deliveryDays: '' }); setAiPrompt(''); setOpen(true); };
  const openEdit = (svc) => { setEditing(svc); setForm({ packageName: svc.packageName, price: svc.price, deliveryDays: svc.deliveryDays }); setOpen(true); };
  const closeModal = () => { setOpen(false); setEditing(null); };

  const submit = (e) => {
    e.preventDefault();
    const payload = { ...form, price: Number(form.price), deliveryDays: Number(form.deliveryDays) };
    if (editing) updateMutation.mutate({ id: editing.id, payload });
    else createMutation.mutate();
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-4">
      <div className="flex justify-end"><button className="btn-primary" onClick={openCreate}><Plus size={16} /> Đăng dịch vụ</button></div>
      {services.length === 0 ? <EmptyState title="Chưa có dịch vụ nào" /> : (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {services.map((svc) => (
            <div key={svc.id} className="card p-5">
              <div className="flex items-start justify-between">
                <p className="font-semibold text-slate-900">{svc.packageName}</p>
                <div className="flex gap-2">
                  <button className="text-slate-400 hover:text-primary-600" onClick={() => openEdit(svc)}>Sửa</button>
                  <button className="text-slate-400 hover:text-red-500" onClick={() => setDeleteTarget(svc)}><Trash2 size={16} /></button>
                </div>
              </div>
              <p className="mt-2 text-sm text-slate-600">{formatCurrency(svc.price)} · Giao trong {svc.deliveryDays} ngày</p>
            </div>
          ))}
        </div>
      )}
      <Modal open={open} onClose={closeModal} title={editing ? 'Cập nhật dịch vụ' : 'Đăng dịch vụ mới'}>
        {!editing && (
          <div className="mb-5 rounded-xl bg-ai-50 p-4">
            <p className="mb-2 flex items-center gap-2 text-sm font-semibold text-ai-700"><Sparkles size={16} /> AI Service Generator</p>
            <div className="flex gap-2">
              <input className="input" placeholder="VD: Tôi làm AI Resume Parser" value={aiPrompt} onChange={(e) => setAiPrompt(e.target.value)} />
              <button type="button" className="btn-ai shrink-0" disabled={!aiPrompt || aiMutation.isPending} onClick={() => aiMutation.mutate()}>Generate</button>
            </div>
          </div>
        )}
        <form className="space-y-4" onSubmit={submit}>
          <Input label="Tên gói dịch vụ" required value={form.packageName} onChange={(e) => setForm({ ...form, packageName: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Giá (USD)" type="number" required value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />
            <Input label="Thời gian giao (ngày)" type="number" required value={form.deliveryDays} onChange={(e) => setForm({ ...form, deliveryDays: e.target.value })} />
          </div>
          <button className="btn-primary w-full" disabled={createMutation.isPending || updateMutation.isPending}>{editing ? 'Lưu' : 'Đăng dịch vụ'}</button>
        </form>
      </Modal>
      <ConfirmDialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} danger title="Xóa dịch vụ"
        message={`Xóa dịch vụ "${deleteTarget?.packageName}"?`} loading={removeMutation.isPending}
        onConfirm={() => removeMutation.mutate(deleteTarget.id)} />
    </div>
  );
}

function ReviewsTab() {
  const user = useAuthStore((s) => s.user);
  const { data: reviews = [], isLoading } = useQuery({
    queryKey: ['reviews', user?.id],
    queryFn: () => reviewsApi.getByUser(user.id),
    enabled: !!user?.id
  });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="card p-6">
      {reviews.length === 0 ? <EmptyState title="Chưa có đánh giá nào" /> : (
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
  );
}
