export default function EmptyState({ icon: Icon, title, description, action }) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 rounded-card border border-dashed border-slate-300 py-16 text-center">
      {Icon && <Icon className="mb-2 text-slate-300" size={40} />}
      <p className="font-semibold text-slate-700">{title}</p>
      {description && <p className="max-w-sm text-sm text-slate-500">{description}</p>}
      {action}
    </div>
  );
}
