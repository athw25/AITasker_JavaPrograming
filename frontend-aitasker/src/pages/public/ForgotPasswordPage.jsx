import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { authApi } from '../../api/auth';
import { errorMessage } from '../../api/client';
import { Input } from '../../components/ui/Field';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);

  const mutation = useMutation({
    mutationFn: () => authApi.forgotPassword(email),
    onSuccess: () => setSent(true),
    onError: (e) => toast.error(errorMessage(e))
  });

  const submit = (e) => {
    e.preventDefault();
    mutation.mutate();
  };

  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Quên mật khẩu</h1>
        <p className="mt-1 text-sm text-slate-500">Nhập email để nhận hướng dẫn đặt lại mật khẩu.</p>

        {sent ? (
          <p className="mt-6 rounded-lg bg-emerald-50 p-4 text-sm text-emerald-700">
            Nếu email tồn tại trong hệ thống, hướng dẫn đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư.
          </p>
        ) : (
          <form className="mt-6 space-y-4" onSubmit={submit}>
            <Input label="Email" type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
            <button className="btn-primary w-full" disabled={mutation.isPending}>
              {mutation.isPending ? 'Đang gửi...' : 'Gửi yêu cầu'}
            </button>
          </form>
        )}

        <p className="mt-5 text-center text-sm text-slate-500">
          Nhớ mật khẩu rồi? <Link to="/login" className="font-semibold text-primary-600">Đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
