import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { Ban, CheckCircle2, Search } from 'lucide-react';
import { adminApi } from '../../api/admin';
import { errorMessage } from '../../api/client';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import Badge from '../../components/ui/Badge';
import { statusBadge, USER_STATUS } from '../../utils/statusMaps';
import { formatDate } from '../../utils/format';

export default function UserManagement() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  const { data: usersPage, isLoading } = useQuery({ queryKey: ['admin', 'users'], queryFn: () => adminApi.getUsers({ page: 0, size: 100 }) });
  const users = usersPage?.content ?? [];

  const banMutation = useMutation({
    mutationFn: adminApi.banUser,
    onSuccess: () => { toast.success('Đã khóa tài khoản'); queryClient.invalidateQueries({ queryKey: ['admin', 'users'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });
  const unbanMutation = useMutation({
    mutationFn: adminApi.unbanUser,
    onSuccess: () => { toast.success('Đã mở khóa tài khoản'); queryClient.invalidateQueries({ queryKey: ['admin', 'users'] }); },
    onError: (e) => toast.error(errorMessage(e))
  });

  const filtered = users.filter((u) =>
    (roleFilter === 'ALL' || u.role === roleFilter) &&
    (u.name?.toLowerCase().includes(keyword.toLowerCase()) || u.email?.toLowerCase().includes(keyword.toLowerCase()))
  );

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">User Management</h1>

      <div className="card flex flex-wrap gap-3 p-4">
        <div className="relative flex-1 min-w-[200px]">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
          <input className="input pl-9" placeholder="Tìm theo tên hoặc email..." value={keyword} onChange={(e) => setKeyword(e.target.value)} />
        </div>
        <select className="input w-40" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
          <option value="ALL">Tất cả vai trò</option>
          <option value="CLIENT">Client</option>
          <option value="EXPERT">Expert</option>
          <option value="ADMIN">Admin</option>
        </select>
      </div>

      <div className="card overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
            <tr>
              <th className="px-5 py-3">Tên</th><th className="px-5 py-3">Email</th><th className="px-5 py-3">Vai trò</th>
              <th className="px-5 py-3">Ngày tạo</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {filtered.map((u) => {
              const s = statusBadge(USER_STATUS, u.status);
              return (
                <tr key={u.id} className="hover:bg-slate-50">
                  <td className="px-5 py-3 font-medium text-slate-800">{u.name}</td>
                  <td className="px-5 py-3">{u.email}</td>
                  <td className="px-5 py-3"><Badge color="blue">{u.role}</Badge></td>
                  <td className="px-5 py-3">{formatDate(u.createdAt)}</td>
                  <td className="px-5 py-3"><Badge color={s.color}>{s.label}</Badge></td>
                  <td className="px-5 py-3 text-right">
                    {u.status === 'BANNED' ? (
                      <button className="btn-secondary !px-3" onClick={() => unbanMutation.mutate(u.id)}><CheckCircle2 size={14} /> Mở khóa</button>
                    ) : (
                      <button className="btn-danger !px-3" onClick={() => banMutation.mutate(u.id)}><Ban size={14} /> Khóa</button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
