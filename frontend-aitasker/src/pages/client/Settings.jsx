import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { authApi } from '../../api/auth';
import { errorMessage } from '../../api/client';
import { useAuthStore } from '../../store/authStore';
import { USER_STATUS, statusBadge } from '../../utils/statusMaps';
import Badge from '../../components/ui/Badge';
import { Input } from '../../components/ui/Field';

export default function ClientSettings() {
  const user = useAuthStore((s) => s.user);
  const setUser = useAuthStore((s) => s.setUser);
  const queryClient = useQueryClient();
  const s = statusBadge(USER_STATUS, user?.status);

  const [form, setForm] = useState({ name: user?.name || '', currentPassword: '', newPassword: '' });

  const updateMutation = useMutation({
    mutationFn: () => authApi.updateMe({
      name: form.name,
      currentPassword: form.currentPassword || undefined,
      newPassword: form.newPassword || undefined
    }),
    onSuccess: (data) => {
      setUser(data);
      queryClient.setQueryData(['currentUser'], data);
      toast.success('Đã cập nhật hồ sơ');
      setForm((f) => ({ ...f, currentPassword: '', newPassword: '' }));
    },
    onError: (err) => toast.error(errorMessage(err))
  });

  return (
    <div className="max-w-xl space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Cài đặt tài khoản</h1>
      <div className="card space-y-4 p-6">
        <div>
          <p className="label">Email</p>
          <p className="text-sm text-slate-800">{user?.email}</p>
        </div>
        <div>
          <p className="label">Vai trò</p>
          <p className="text-sm text-slate-800">{user?.role}</p>
        </div>
        <div>
          <p className="label">Trạng thái</p>
          <Badge color={s.color}>{s.label}</Badge>
        </div>
      </div>

      <div className="card p-6">
        <h3 className="mb-4 font-semibold text-slate-900">Cập nhật thông tin</h3>
        <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); updateMutation.mutate(); }}>
          <Input label="Họ và tên" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <Input label="Mật khẩu hiện tại (chỉ cần nếu đổi mật khẩu)" type="password"
            value={form.currentPassword} onChange={(e) => setForm({ ...form, currentPassword: e.target.value })} />
          <Input label="Mật khẩu mới (bỏ trống nếu không đổi)" type="password"
            value={form.newPassword} onChange={(e) => setForm({ ...form, newPassword: e.target.value })} />
          <button className="btn-primary" disabled={updateMutation.isPending}>
            {updateMutation.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
          </button>
        </form>
      </div>
    </div>
  );
}
