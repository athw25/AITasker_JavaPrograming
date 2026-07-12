import { Link } from 'react-router-dom';
import { Input } from '../../components/ui/Field';

export default function ForgotPasswordPage() {
  return (
    <div className="mx-auto flex min-h-[80vh] max-w-md flex-col justify-center px-6 py-12">
      <div className="card p-8">
        <h1 className="text-2xl font-bold text-slate-900">Quên mật khẩu</h1>
        <p className="mt-1 text-sm text-slate-500">
          Tính năng đặt lại mật khẩu qua email hiện chưa được hỗ trợ. Vui lòng liên hệ Admin để được hỗ trợ đặt lại mật khẩu.
        </p>
        <div className="mt-6 space-y-4 opacity-50">
          <Input label="Email" type="email" disabled placeholder="you@example.com" />
          <button className="btn-primary w-full" disabled>Gửi yêu cầu</button>
        </div>
        <p className="mt-5 text-center text-sm text-slate-500">
          <Link to="/login" className="font-semibold text-primary-600">Quay lại đăng nhập</Link>
        </p>
      </div>
    </div>
  );
}
