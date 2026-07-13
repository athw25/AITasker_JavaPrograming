import { Link } from 'react-router-dom';
import { Bot, Sparkles, ShieldCheck, Users, ArrowRight } from 'lucide-react';
import { useAuthStore } from '../../store/authStore';

const FEATURES = [
    { icon: Sparkles, title: 'AI Job Assistant', desc: 'Mô tả ý tưởng, AI tự sinh tiêu đề, mô tả, kỹ năng và ngân sách đề xuất.' },
    { icon: Users, title: 'AI Expert Recommendation', desc: 'Ghép nối chuyên gia phù hợp nhất dựa trên kỹ năng, đánh giá và tỷ lệ thành công.' },
    { icon: ShieldCheck, title: 'Escrow & Trust Platform', desc: 'Thanh toán ký quỹ an toàn, milestone rõ ràng, xử lý tranh chấp minh bạch.' }
];

export default function LandingPage() {
    const isAuthenticated = useAuthStore((s) => s.isAuthenticated());

    return (
        <div>
            <section className="mx-auto max-w-6xl px-6 py-20 text-center">
                <span className="badge bg-ai-100 text-ai-700 mb-4">AI Services Marketplace</span>
                <h1 className="text-4xl font-extrabold text-slate-900 sm:text-5xl">
                    Kết nối doanh nghiệp với<br /><span className="text-primary-600">chuyên gia AI hàng đầu</span>
                </h1>
                <p className="mx-auto mt-5 max-w-2xl text-slate-500">
                    AITasker giúp bạn tạo yêu cầu dự án bằng AI, tìm đúng chuyên gia, quản lý dự án
                    và thanh toán an toàn — tất cả trên một nền tảng duy nhất.
                </p>
                <div className="mt-8 flex justify-center gap-3">
                    {isAuthenticated ? (
                        <Link to="/app" className="btn-primary px-6 py-3 text-base">
                            Vào Dashboard <ArrowRight size={18} />
                        </Link>
                    ) : (
                        <>
                            <Link to="/register" className="btn-primary px-6 py-3 text-base">
                                Bắt đầu ngay <ArrowRight size={18} />
                            </Link>
                            <Link to="/login" className="btn-secondary px-6 py-3 text-base">Đăng nhập</Link>
                        </>
                    )}
                </div>
            </section>

            <section className="bg-white py-16">
                <div className="mx-auto grid max-w-6xl grid-cols-1 gap-6 px-6 sm:grid-cols-3">
                    {FEATURES.map((f) => (
                        <div key={f.title} className="card p-6">
                            <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-xl bg-primary-50 text-primary-600">
                                <f.icon size={22} />
                            </div>
                            <h3 className="font-semibold text-slate-900">{f.title}</h3>
                            <p className="mt-2 text-sm text-slate-500">{f.desc}</p>
                        </div>
                    ))}
                </div>
            </section>

            <section className="mx-auto max-w-6xl px-6 py-16 text-center">
                <div className="flex justify-center"><Bot size={40} className="text-primary-600" /></div>
                <h2 className="mt-4 text-2xl font-bold text-slate-900">Sẵn sàng triển khai giải pháp AI?</h2>
                <p className="mt-2 text-slate-500">
                    {isAuthenticated ? 'Truy cập không gian làm việc của bạn để quản lý các dự án.' : 'Đăng ký miễn phí và đăng dự án đầu tiên trong vài phút.'}
                </p>
                {isAuthenticated ? (
                    <Link to="/app" className="btn-primary mt-6 inline-flex px-6 py-3 text-base">Vào không gian làm việc</Link>
                ) : (
                    <Link to="/register" className="btn-primary mt-6 inline-flex px-6 py-3 text-base">Tạo tài khoản</Link>
                )}
            </section>
        </div>
    );
}
