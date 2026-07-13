import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useRegister } from '../../hooks/useAuth';
import { Input, Select } from '../../components/ui/Field';

export default function RegisterPage() {
  const [form, setForm] = useState({ fullName: '', email: '', password: '', role: 'CLIENT' });
  const register = useRegister();

  const submit = (e) => {
    e.preventDefault();
    register.mutate(form);
  };

  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Tạo tài khoản</h1>
        <p className="mt-1 text-sm text-slate-500">Tham gia AITasker ngay hôm nay.</p>
        <form className="mt-6 space-y-4" onSubmit={submit}>
          <Select label="Bạn là" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
            <option value="CLIENT">Doanh nghiệp / Client</option>
            <option value="EXPERT">Chuyên gia AI / Expert</option>
          </Select>
          <Input label="Họ và tên" required value={form.fullName}
            onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
          <Input label="Email" type="email" required value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <Input label="Mật khẩu" type="password" required minLength={6} value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })} />
          <button className="btn-primary w-full" disabled={register.isPending}>
            {register.isPending ? 'Đang tạo...' : 'Đăng ký'}
          </button>
        </form>
        <p className="mt-5 text-center text-sm text-slate-500">
          Đã có tài khoản? <Link to="/login" className="font-semibold text-primary-600">Đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
