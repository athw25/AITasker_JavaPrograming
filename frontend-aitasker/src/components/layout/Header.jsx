import { useState } from 'react';
import { Bell, LogOut, User as UserIcon } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { notificationsApi } from '../../api/notifications';
import { useAuthStore } from '../../store/authStore';
import { useLogout } from '../../hooks/useAuth';
import { timeAgo } from '../../utils/format';

export default function Header({ homePath }) {
  const [open, setOpen] = useState(false);
  const user = useAuthStore((s) => s.user);
  const logout = useLogout();
  const queryClient = useQueryClient();

  const { data: notifications = [] } = useQuery({
    queryKey: ['notifications'],
    queryFn: notificationsApi.getAll,
    refetchInterval: 15000
  });

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  const markRead = async (id) => {
    await notificationsApi.markAsRead(id);
    queryClient.invalidateQueries({ queryKey: ['notifications'] });
  };

  return (
    <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-6">
      <Link to={homePath} className="text-sm font-semibold text-slate-500">AI Services Marketplace</Link>
      <div className="flex items-center gap-4">
        <div className="relative">
          <button onClick={() => setOpen((v) => !v)} className="relative rounded-full p-2 text-slate-500 hover:bg-slate-100">
            <Bell size={20} />
            {unreadCount > 0 && (
              <span className="absolute -right-0.5 -top-0.5 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
                {unreadCount}
              </span>
            )}
          </button>
          {open && (
            <div className="absolute right-0 z-40 mt-2 w-80 rounded-card border border-slate-200 bg-white shadow-lg">
              <div className="border-b border-slate-100 px-4 py-3 text-sm font-semibold">Thông báo</div>
              <div className="max-h-80 overflow-y-auto">
                {notifications.length === 0 && <p className="px-4 py-6 text-center text-sm text-slate-400">Không có thông báo</p>}
                {notifications.map((n) => (
                  <button
                    key={n.id}
                    onClick={() => markRead(n.id)}
                    className={`block w-full border-b border-slate-50 px-4 py-3 text-left text-sm hover:bg-slate-50 ${!n.isRead ? 'bg-primary-50/40' : ''}`}
                  >
                    <p className="font-medium text-slate-800">{n.title}</p>
                    <p className="mt-0.5 text-xs text-slate-500 line-clamp-2">{n.content}</p>
                    <p className="mt-1 text-[11px] text-slate-400">{timeAgo(n.createdAt)}</p>
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-slate-200 text-slate-600">
            <UserIcon size={16} />
          </div>
          <span className="hidden text-sm font-medium text-slate-700 sm:block">{user?.name}</span>
        </div>
        <button onClick={logout} className="rounded-full p-2 text-slate-500 hover:bg-slate-100" title="Đăng xuất">
          <LogOut size={18} />
        </button>
      </div>
    </header>
  );
}
