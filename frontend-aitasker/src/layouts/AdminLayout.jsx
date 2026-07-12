import { Outlet } from 'react-router-dom';
import { LayoutDashboard, Users, Store, ShieldAlert, BarChart3, ScrollText } from 'lucide-react';
import Sidebar from '../components/layout/Sidebar';
import Header from '../components/layout/Header';

const items = [
  { to: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/admin/users', label: 'User Management', icon: Users },
  { to: '/admin/marketplace', label: 'Marketplace', icon: Store },
  { to: '/admin/disputes', label: 'Disputes', icon: ShieldAlert },
  { to: '/admin/analytics', label: 'Analytics & Reports', icon: BarChart3 },
  { to: '/admin/audit-logs', label: 'Audit Logs', icon: ScrollText }
];

export default function AdminLayout() {
  return (
    <div className="flex h-screen bg-slate-50">
      <Sidebar items={items} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header homePath="/admin/dashboard" />
        <main className="flex-1 overflow-y-auto p-6"><Outlet /></main>
      </div>
    </div>
  );
}
