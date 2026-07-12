import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { projectsApi } from '../../api/projects';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import { statusBadge, PROJECT_STATUS } from '../../utils/statusMaps';
import { formatDate } from '../../utils/format';

export default function ProjectList({ role }) {
  const { data: projects = [], isLoading } = useQuery({
    queryKey: ['projects', role],
    queryFn: role === 'CLIENT' ? projectsApi.getClientProjects : projectsApi.getExpertProjects
  });

  const basePath = role === 'CLIENT' ? '/client/projects' : '/expert/projects';

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Projects</h1>
      {projects.length === 0 ? (
        <EmptyState title="Chưa có dự án nào" description="Dự án sẽ xuất hiện sau khi một đề xuất được chấp nhận." />
      ) : (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {projects.map((p) => {
            const s = statusBadge(PROJECT_STATUS, p.status);
            return (
              <Link key={p.id} to={`${basePath}/${p.id}`} className="card p-5 hover:border-primary-300">
                <div className="flex items-start justify-between">
                  <p className="font-semibold text-slate-900">{p.jobTitle}</p>
                  <Badge color={s.color}>{s.label}</Badge>
                </div>
                <p className="mt-1 text-sm text-slate-500">
                  {role === 'CLIENT' ? `Expert: ${p.expertName}` : `Client: ${p.clientName}`}
                </p>
                <div className="mt-3 flex justify-between text-sm text-slate-600">
                  <span>📌 Milestone: {p.completedMilestones}/{p.totalMilestones}</span>
                  <span>📅 {formatDate(p.startDate)}</span>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
