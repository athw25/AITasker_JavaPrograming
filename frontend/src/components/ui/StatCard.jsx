export default function StatCard({ icon: Icon, label, value, color = 'primary' }) {
  return (
    <div className="card flex items-center gap-4 p-5">
      <div className={`flex h-11 w-11 items-center justify-center rounded-xl bg-${color}-50 text-${color}-600`}>
        {Icon && <Icon size={22} />}
      </div>
      <div>
        <p className="text-xs font-medium text-slate-500">{label}</p>
        <p className="text-xl font-bold text-slate-900">{value}</p>
      </div>
    </div>
  );
}
