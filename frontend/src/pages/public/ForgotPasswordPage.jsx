import { useState } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { authApi } from '../../api/auth';
import { errorMessage } from '../../api/client';
import { Input } from '../../components/ui/Field';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApi.forgotPassword(email);
      setSent(true);
    } catch (err) {
      toast.error(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Quên mật khẩu</h1>
        {sent ? (
          <p className="mt-3 text-sm text-slate-600">
            Nếu email này tồn tại trong hệ thống, hướng dẫn đặt lại mật khẩu đã được gửi tới hộp thư của bạn.
          </p>
        ) : (
          <>
            <p className="mt-1 text-sm text-slate-500">Nhập email để nhận link đặt lại mật khẩu.</p>
            <form className="mt-6 space-y-4" onSubmit={submit}>
              <Input label="Email" type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
              <button className="btn-primary w-full" disabled={loading}>
                {loading ? 'Đang gửi...' : 'Gửi yêu cầu'}
              </button>
            </form>
          </>
        )}
        <p className="mt-5 text-center text-sm text-slate-500">
          <Link to="/login" className="font-semibold text-primary-600">Quay lại đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
