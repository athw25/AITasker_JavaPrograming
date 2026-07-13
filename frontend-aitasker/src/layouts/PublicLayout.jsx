import { Outlet, Link } from 'react-router-dom';
import { Bot } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { useLogout } from '../hooks/useAuth';

export default function PublicLayout() {
    const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
    const logout = useLogout();

    return (
        <div className="flex min-h-screen flex-col bg-slate-50">
            <header className="border-b border-slate-200 bg-white">
                <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
                    <Link to="/" className="flex items-center gap-2">
                        <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary-600 text-white">
                            <Bot size={20} />
                        </div>
                        <span className="text-lg font-bold text-slate-900">AITasker</span>
                    </Link>
                    <div className="flex gap-3">
                        {isAuthenticated ? (
                            <>
                                <Link to="/app" className="btn-primary">Vào Dashboard</Link>
                                <button onClick={logout} className="btn-secondary">Đăng xuất</button>
                            </>
                        ) : (
                            <>
                                <Link to="/login" className="btn-secondary">Đăng nhập</Link>
                                <Link to="/register" className="btn-primary">Đăng ký</Link>
                            </>
                        )}
                    </div>
                </div>
            </header>
            <main className="flex-1"><Outlet /></main>
            <footer className="border-t border-slate-200 bg-white py-6 text-center text-sm text-slate-400">
                © 2026 AITasker — AI Services Marketplace Platform
            </footer>
        </div>
    );
}
