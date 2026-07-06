import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search } from 'lucide-react';
import { adminApi } from '../../api/admin';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import { formatDateTime } from '../../utils/format';

export default function AuditLogs() {
  const [keyword, setKeyword] = useState('');
  const { data: logs = [], isLoading } = useQuery({ queryKey: ['admin', 'auditLogs'], queryFn: adminApi.getAuditLogs });

  const filtered = logs.filter((l) => l.action?.toLowerCase().includes(keyword.toLowerCase()));

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Audit Logs</h1>

      <div className="card relative p-4">
        <Search className="absolute left-7 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
        <input className="input pl-9" placeholder="Lọc theo hành động (LOGIN, PAYMENT_DEPOSIT...)" value={keyword} onChange={(e) => setKeyword(e.target.value)} />
      </div>

      {filtered.length === 0 ? <EmptyState title="Không có audit log nào" /> : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr><th className="px-5 py-3">Người thực hiện</th><th className="px-5 py-3">Hành động</th><th className="px-5 py-3">Mô tả</th><th className="px-5 py-3">Thời gian</th></tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filtered.map((l) => (
                <tr key={l.id}>
                  <td className="px-5 py-3">{l.actor?.name || l.actor?.email || 'Hệ thống'}</td>
                  <td className="px-5 py-3"><Badge color="blue">{l.action}</Badge></td>
                  <td className="px-5 py-3 text-slate-500">{l.description}</td>
                  <td className="px-5 py-3">{formatDateTime(l.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
