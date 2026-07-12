import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { projectsApi } from '../../api/projects';
import { milestonesApi } from '../../api/milestones';
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import EmptyState from '../../components/ui/EmptyState';
import Badge from '../../components/ui/Badge';
import { statusBadge, MILESTONE_STATUS } from '../../utils/statusMaps';
import { formatCurrency } from '../../utils/format';

function ProjectMilestoneRows({ project }) {
  const { data: milestones = [] } = useQuery({
    queryKey: ['milestones', project.id],
    queryFn: () => milestonesApi.getByProject(project.id)
  });
  return milestones.map((m) => (
    <tr key={m.id} className="hover:bg-slate-50">
      <td className="px-5 py-3">
        <Link to={`/client/projects/${project.id}`} className="font-medium text-primary-700 hover:underline">{project.jobTitle}</Link>
      </td>
      <td className="px-5 py-3">{m.title}</td>
      <td className="px-5 py-3">{formatCurrency(m.amount)}</td>
      <td className="px-5 py-3"><Badge color={statusBadge(MILESTONE_STATUS, m.status).color}>{statusBadge(MILESTONE_STATUS, m.status).label}</Badge></td>
    </tr>
  ));
}

export default function Transactions() {
  const { data: projects = [], isLoading } = useQuery({ queryKey: ['projects', 'CLIENT'], queryFn: projectsApi.getClientProjects });

  if (isLoading) return <LoadingSpinner />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-slate-900">Lịch sử giao dịch</h1>
      {projects.length === 0 ? <EmptyState title="Chưa có giao dịch nào" /> : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr>
                <th className="px-5 py-3">Dự án</th>
                <th className="px-5 py-3">Milestone</th>
                <th className="px-5 py-3">Số tiền</th>
                <th className="px-5 py-3">Trạng thái</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {projects.map((p) => <ProjectMilestoneRows key={p.id} project={p} />)}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
