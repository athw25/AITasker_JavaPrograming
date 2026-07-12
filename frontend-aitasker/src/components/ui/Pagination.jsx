export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null;
  return (
    <div className="flex items-center justify-center gap-2 py-4">
      <button className="btn-secondary" disabled={page <= 0} onClick={() => onChange(page - 1)}>Trước</button>
      <span className="text-sm text-slate-600">Trang {page + 1} / {totalPages}</span>
      <button className="btn-secondary" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}>Sau</button>
    </div>
  );
}
