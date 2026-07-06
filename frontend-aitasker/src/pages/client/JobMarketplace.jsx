import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Plus, Search, Sparkles, Eye, Pencil, Trash2 } from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import { aiApi } from '../../api/ai';
import { errorMessage } from '../../api/client';
import Modal from '../../components/ui/Modal';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { Input, Textarea, Select } from '../../components/ui/Field';
import Badge from '../../components/ui/Badge';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import { statusBadge, JOB_STATUS } from '../../utils/statusMaps';
import { formatCurrency, formatDate } from '../../utils/format';

const EMPTY_FORM = { title: '', description: '', budget: '', deadline: '', requiredSkills: '' };

export default function JobMarketplace() {
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState({ keyword: '', status: '', minBudget: '', maxBudget: '' });
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [aiPrompt, setAiPrompt] = useState('');
  const [deleteTarget, setDeleteTarget] = useState(null);

  const isSearching = filters.keyword || filters.status || filters.minBudget || filters.maxBudget;

  const { data: jobs = [], isLoading } = useQuery({
    queryKey: ['jobs', 'mine'],
    queryFn: jobsApi.getMine,
    enabled: !isSearching
  });

  const { data: searchResults = [], isLoading: searching } = useQuery({
    queryKey: ['jobs', 'search', filters],
    queryFn: () => jobsApi.search(Object.fromEntries(Object.entries(filters).filter(([, v]) => v))),
    enabled: !!isSearching
  });

  const list = isSearching ? searchResults : jobs;

  const createMutation = useMutation({
    mutationFn: jobsApi.create,
    onSuccess: () => {
      toast.success('Đăng job thành công!');
      queryClient.invalidateQueries({ queryKey: ['jobs'] });
      closeModal();
    },
    onError: (e) => toast.error(errorMessage(e))
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }) => jobsApi.update(id, payload),
    onSuccess: () => {
      toast.success('Cập nhật job thành công!');
      queryClient.invalidateQueries({ queryKey: ['jobs'] });
      closeModal();
    },
    onError: (e) => toast.error(errorMessage(e))
  });

  const deleteMutation = useMutation({
    mutationFn: jobsApi.remove,
    onSuccess: () => {
      toast.success('Đã xóa job');
      queryClient.invalidateQueries({ queryKey: ['jobs'] });
      setDeleteTarget(null);
    },
    onError: (e) => toast.error(errorMessage(e))
  });

  const aiMutation = useMutation({
    mutationFn: () => aiApi.jobAssistant(aiPrompt),
    onSuccess: (data) => {
      setForm({
        title: data.title || '',
        description: data.description || '',
        budget: data.budget ?? '',
        deadline: form.deadline,
        requiredSkills: (data.skills || []).join(', ')
      });
      toast.success('AI đã sinh nội dung, bạn có thể chỉnh sửa trước khi lưu.');
    },
    onError: (e) => toast.error(errorMessage(e))
  });

  const openCreate = () => { setEditing(null); setForm(EMPTY_FORM); setAiPrompt(''); setModalOpen(true); };
  const openEdit = (job) => {
    setEditing(job);
    setForm({ title: job.title, description: job.description, budget: job.budget, deadline: job.deadline, requiredSkills: job.requiredSkills });
    setModalOpen(true);
  };
  const closeModal = () => { setModalOpen(false); setEditing(null); };

  const submit = (e) => {
    e.preventDefault();
    const payload = { ...form, budget: Number(form.budget) };
    if (editing) updateMutation.mutate({ id: editing.id, payload });
    else createMutation.mutate(payload);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-slate-900">Job Marketplace</h1>
        <button className="btn-primary" onClick={openCreate}><Plus size={16} /> Đăng Job mới</button>
      </div>

      <div className="card grid grid-cols-1 gap-3 p-4 sm:grid-cols-4">
        <div className="relative sm:col-span-2">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
          <input className="input pl-9" placeholder="Tìm theo tiêu đề, kỹ năng..."
            value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} />
        </div>
        <input className="input" type="number" placeholder="Ngân sách từ"
          value={filters.minBudget} onChange={(e) => setFilters({ ...filters, minBudget: e.target.value })} />
        <input className="input" type="number" placeholder="Ngân sách đến"
          value={filters.maxBudget} onChange={(e) => setFilters({ ...filters, maxBudget: e.target.value })} />
      </div>

      {(isLoading || searching) ? <LoadingSpinner /> : list.length === 0 ? (
        <EmptyState title="Chưa có job nào" description="Đăng job đầu tiên để bắt đầu tìm chuyên gia AI phù hợp." />
      ) : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr>
                <th className="px-5 py-3">Tiêu đề</th>
                <th className="px-5 py-3">Ngân sách</th>
                <th className="px-5 py-3">Hạn chót</th>
                <th className="px-5 py-3">Trạng thái</th>
                <th className="px-5 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {list.map((job) => {
                const s = statusBadge(JOB_STATUS, job.status);
                return (
                  <tr key={job.id} className="hover:bg-slate-50">
                    <td className="px-5 py-3 font-medium text-slate-800">{job.title}</td>
                    <td className="px-5 py-3">{formatCurrency(job.budget)}</td>
                    <td className="px-5 py-3">{formatDate(job.deadline)}</td>
                    <td className="px-5 py-3"><Badge color={s.color}>{s.label}</Badge></td>
                    <td className="px-5 py-3">
                      <div className="flex justify-end gap-2">
                        <Link to={`/client/jobs/${job.id}`} className="btn-ghost !px-2"><Eye size={16} /></Link>
                        <button className="btn-ghost !px-2" onClick={() => openEdit(job)}><Pencil size={16} /></button>
                        <button className="btn-ghost !px-2 text-red-500" onClick={() => setDeleteTarget(job)}><Trash2 size={16} /></button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={modalOpen} onClose={closeModal} title={editing ? 'Cập nhật Job' : 'Đăng Job mới'} size="lg">
        <div className="mb-5 rounded-xl bg-ai-50 p-4">
          <p className="mb-2 flex items-center gap-2 text-sm font-semibold text-ai-700"><Sparkles size={16} /> AI Job Assistant</p>
          <div className="flex gap-2">
            <input className="input" placeholder="VD: Tôi muốn chatbot bán hàng Facebook"
              value={aiPrompt} onChange={(e) => setAiPrompt(e.target.value)} />
            <button type="button" className="btn-ai shrink-0" disabled={!aiPrompt || aiMutation.isPending}
              onClick={() => aiMutation.mutate()}>
              {aiMutation.isPending ? 'Đang sinh...' : 'Generate'}
            </button>
          </div>
        </div>
        <form className="space-y-4" onSubmit={submit}>
          <Input label="Tiêu đề" required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <Textarea label="Mô tả" required value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Ngân sách (USD)" type="number" required value={form.budget} onChange={(e) => setForm({ ...form, budget: e.target.value })} />
            <Input label="Hạn chót" type="date" required value={form.deadline} onChange={(e) => setForm({ ...form, deadline: e.target.value })} />
          </div>
          <Input label="Kỹ năng yêu cầu (phân tách bởi dấu phẩy)" required value={form.requiredSkills}
            onChange={(e) => setForm({ ...form, requiredSkills: e.target.value })} />
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" className="btn-secondary" onClick={closeModal}>Hủy</button>
            <button className="btn-primary" disabled={createMutation.isPending || updateMutation.isPending}>
              {editing ? 'Lưu thay đổi' : 'Đăng Job'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)} danger
        title="Xóa Job" message={`Bạn có chắc muốn xóa job "${deleteTarget?.title}"?`}
        loading={deleteMutation.isPending} onConfirm={() => deleteMutation.mutate(deleteTarget.id)} />
    </div>
  );
}
