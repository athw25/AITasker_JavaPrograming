import { Outlet } from 'react-router-dom';
import { LayoutDashboard, Briefcase, FolderKanban, Receipt, Settings } from 'lucide-react';
import Sidebar from '../components/layout/Sidebar';
import Header from '../components/layout/Header';

const items = [
  { to: '/client/dashboard', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/client/jobs', label: 'Job Marketplace', icon: Briefcase },
  { to: '/client/projects', label: 'Projects', icon: FolderKanban },
  { to: '/client/transactions', label: 'Transactions', icon: Receipt },
  { to: '/client/settings', label: 'Settings', icon: Settings }
];

export default function ClientLayout() {
  return (
    <div className="flex h-screen bg-slate-50">
      <Sidebar items={items} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header homePath="/client/dashboard" />
        <main className="flex-1 overflow-y-auto p-6"><Outlet /></main>
      </div>
    </div>
  );
}
