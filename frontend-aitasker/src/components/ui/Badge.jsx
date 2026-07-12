const COLORS = {
  gray: 'bg-slate-100 text-slate-700',
  blue: 'bg-blue-100 text-blue-700',
  green: 'bg-emerald-100 text-emerald-700',
  red: 'bg-red-100 text-red-700',
  orange: 'bg-orange-100 text-orange-700',
  purple: 'bg-purple-100 text-purple-700'
};

export default function Badge({ color = 'gray', children }) {
  return <span className={`badge ${COLORS[color] || COLORS.gray}`}>{children}</span>;
}
