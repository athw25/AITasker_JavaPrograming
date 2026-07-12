import { useAuthStore } from '../../store/authStore';
import { USER_STATUS, statusBadge } from '../../utils/statusMaps';
import Badge from '../../components/ui/Badge';

export default function ClientSettings() {
  const user = useAuthStore((s) => s.user);
  const s = statusBadge(USER_STATUS, user?.status);

  return (
    <div className="max-w-xl space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Cài đặt tài khoản</h1>
      <div className="card space-y-4 p-6">
        <div>
          <p className="label">Họ và tên</p>
          <p className="text-sm text-slate-800">{user?.name}</p>
        </div>
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
    </div>
  );
}
