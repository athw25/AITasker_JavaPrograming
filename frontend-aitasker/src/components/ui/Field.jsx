export function Input({ label, error, ...props }) {
  return (
    <div>
      {label && <label className="label">{label}</label>}
      <input className="input" {...props} />
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  );
}

export function Textarea({ label, error, rows = 4, ...props }) {
  return (
    <div>
      {label && <label className="label">{label}</label>}
      <textarea className="input" rows={rows} {...props} />
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  );
}

export function Select({ label, error, children, ...props }) {
  return (
    <div>
      {label && <label className="label">{label}</label>}
      <select className="input" {...props}>{children}</select>
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  );
}
