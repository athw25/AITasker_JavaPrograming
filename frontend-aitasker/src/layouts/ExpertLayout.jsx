import { Outlet } from 'react-router-dom';
import { LayoutDashboard, UserSquare2, Search, FolderKanban, Wallet, Settings } from 'lucide-react';
import Sidebar from '../components/layout/Sidebar';
import Header from '../components/layout/Header';

const items = [
  { to: '/expert/dashboard', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/expert/profile', label: 'Expert Profile', icon: UserSquare2 },
  { to: '/expert/browse-jobs', label: 'Browse Jobs', icon: Search },
  { to: '/expert/projects', label: 'Projects', icon: FolderKanban },
  { to: '/expert/earnings', label: 'Withdraw & Earnings', icon: Wallet },
  { to: '/expert/settings', label: 'Settings', icon: Settings }
];

export default function ExpertLayout() {
  return (
    <div className="flex h-screen bg-slate-50">
      <Sidebar items={items} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header homePath="/expert/dashboard" />
        <main className="flex-1 overflow-y-auto p-6"><Outlet /></main>
      </div>
    </div>
  );
}
