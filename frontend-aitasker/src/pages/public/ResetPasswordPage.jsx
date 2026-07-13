import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { authApi } from '../../api/auth';
import { errorMessage } from '../../api/client';
import { Input } from '../../components/ui/Field';

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token') || '';
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const mutation = useMutation({
    mutationFn: () => authApi.resetPassword(token, newPassword),
    onSuccess: () => {
      toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.');
      navigate('/login', { replace: true });
    },
    onError: (e) => toast.error(errorMessage(e))
  });

  const submit = (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      toast.error('Mật khẩu xác nhận không khớp');
      return;
    }
    mutation.mutate();
  };

  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Đặt lại mật khẩu</h1>

        {!token ? (
          <p className="mt-6 rounded-lg bg-red-50 p-4 text-sm text-red-700">
            Liên kết không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu lại.
          </p>
        ) : (
          <form className="mt-6 space-y-4" onSubmit={submit}>
            <Input label="Mật khẩu mới" type="password" required minLength={6}
              value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
            <Input label="Xác nhận mật khẩu mới" type="password" required minLength={6}
              value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />
            <button className="btn-primary w-full" disabled={mutation.isPending}>
              {mutation.isPending ? 'Đang xử lý...' : 'Đặt lại mật khẩu'}
            </button>
          </form>
        )}

        <p className="mt-5 text-center text-sm text-slate-500">
          <Link to="/login" className="font-semibold text-primary-600">Quay lại đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
