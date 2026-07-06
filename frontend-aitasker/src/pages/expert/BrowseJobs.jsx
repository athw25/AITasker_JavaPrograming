import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Search } from 'lucide-react';
import { jobsApi } from '../../api/jobs';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import { statusBadge, JOB_STATUS } from '../../utils/statusMaps';
import { formatCurrency, formatDate } from '../../utils/format';

export default function BrowseJobs() {
  const [filters, setFilters] = useState({ keyword: '', skills: '', minBudget: '', maxBudget: '' });
  const isSearching = Object.values(filters).some(Boolean);

  const { data: allJobs = [], isLoading: l1 } = useQuery({ queryKey: ['jobs', 'all'], queryFn: jobsApi.getAll, enabled: !isSearching });
  const { data: searchResults = [], isLoading: l2 } = useQuery({
    queryKey: ['jobs', 'search', filters],
    queryFn: () => jobsApi.search(Object.fromEntries(Object.entries(filters).filter(([, v]) => v))),
    enabled: isSearching
  });

  const jobs = (isSearching ? searchResults : allJobs).filter((j) => j.status === 'OPEN');
  const loading = isSearching ? l2 : l1;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Browse Jobs</h1>

      <div className="card grid grid-cols-1 gap-3 p-4 sm:grid-cols-4">
        <div className="relative sm:col-span-2">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
          <input className="input pl-9" placeholder="Tìm theo tiêu đề..." value={filters.keyword}
            onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} />
        </div>
        <input className="input" placeholder="Kỹ năng (vd: Python)" value={filters.skills}
          onChange={(e) => setFilters({ ...filters, skills: e.target.value })} />
        <input className="input" type="number" placeholder="Ngân sách tối thiểu" value={filters.minBudget}
          onChange={(e) => setFilters({ ...filters, minBudget: e.target.value })} />
      </div>

      {loading ? <LoadingSpinner /> : jobs.length === 0 ? (
        <EmptyState title="Không tìm thấy job phù hợp" />
      ) : (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {jobs.map((job) => {
            const s = statusBadge(JOB_STATUS, job.status);
            return (
              <Link to={`/expert/jobs/${job.id}`} key={job.id} className="card p-5 hover:border-primary-300">
                <div className="flex items-start justify-between">
                  <p className="font-semibold text-slate-900">{job.title}</p>
                  <Badge color={s.color}>{s.label}</Badge>
                </div>
                <p className="mt-2 line-clamp-2 text-sm text-slate-500">{job.description}</p>
                <div className="mt-3 flex justify-between text-sm text-slate-600">
                  <span>💰 {formatCurrency(job.budget)}</span>
                  <span>📅 {formatDate(job.deadline)}</span>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
