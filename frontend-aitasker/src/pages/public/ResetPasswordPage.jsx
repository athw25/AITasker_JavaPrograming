import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { authApi } from '../../api/auth';
import { errorMessage } from '../../api/client';
import { Input } from '../../components/ui/Field';

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';
  const navigate = useNavigate();

  const [newPassword, setNewPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApi.resetPassword(token, newPassword);
      toast.success('Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.');
      navigate('/login');
    } catch (err) {
      toast.error(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Đặt lại mật khẩu</h1>
        {!token ? (
          <p className="mt-3 text-sm text-red-600">Link không hợp lệ hoặc thiếu token.</p>
        ) : (
          <form className="mt-6 space-y-4" onSubmit={submit}>
            <Input label="Mật khẩu mới" type="password" required minLength={6}
              value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
            <button className="btn-primary w-full" disabled={loading}>
              {loading ? 'Đang xử lý...' : 'Đặt lại mật khẩu'}
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
