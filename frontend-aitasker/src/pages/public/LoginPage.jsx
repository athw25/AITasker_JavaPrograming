import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useLogin } from '../../hooks/useAuth';
import { Input } from '../../components/ui/Field';

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' });
  const login = useLogin();

  const submit = (e) => {
    e.preventDefault();
    login.mutate(form);
  };

  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Đăng nhập</h1>
        <p className="mt-1 text-sm text-slate-500">Chào mừng quay lại AITasker.</p>
        <form className="mt-6 space-y-4" onSubmit={submit}>
          <Input label="Email" type="email" required value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <Input label="Mật khẩu" type="password" required value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })} />
          <button className="btn-primary w-full" disabled={login.isPending}>
            {login.isPending ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>
        <p className="mt-5 text-center text-sm text-slate-500">
          Chưa có tài khoản? <Link to="/register" className="font-semibold text-primary-600">Đăng ký</Link>
        </p>
      </div>
    </div>
  );
}
