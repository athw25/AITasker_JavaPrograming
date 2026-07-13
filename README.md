# AITasker

## AI Service Marketplace Platform

AITasker là nền tảng Marketplace chuyên biệt kết nối doanh nghiệp, startup và nhà sáng lập không chuyên kỹ thuật với các chuyên gia Trí tuệ nhân tạo (AI Expert), nhằm hỗ trợ triển khai các giải pháp AI vào thực tế. Hệ thống tích hợp AI trực tiếp vào quy trình vận hành marketplace (AI Job Assistant, AI Service Generator, AI Expert Recommendation) thay vì chỉ đóng vai trò trung gian đơn thuần.

Dự án phát triển theo hình thức Research-Based Learning, được xây dựng qua 5 giai đoạn (Phase 1–5), hiện đã hoàn thiện **Backend** (đầy đủ chức năng cốt lõi + đã kiểm thử) và **Frontend** (React, đã đồng bộ tương thích với Backend).

---

## 1. Bối cảnh & Vấn đề

Doanh nghiệp nhỏ và nhà sáng lập không chuyên kỹ thuật gặp khó khăn khi triển khai AI: thiếu chuyên môn, khó mô tả yêu cầu dự án, chưa có nền tảng chuyên biệt để thuê ngoài dịch vụ AI. Ngược lại, chuyên gia AI thiếu kênh tiếp cận khách hàng uy tín, và các nền tảng freelancer hiện có quá tổng quát, không tối ưu cho dịch vụ AI.

AITasker giải quyết khoảng trống này bằng: hệ thống hỗ trợ sinh yêu cầu bằng AI, hệ thống đề xuất chuyên gia thông minh, cơ chế thanh toán ký quỹ (Escrow) an toàn, và quản lý toàn bộ vòng đời dự án từ đầu đến cuối.

## 2. Mục tiêu dự án

- Kết nối khách hàng với chuyên gia AI phù hợp, giảm mơ hồ trong việc xác định yêu cầu dự án
- Tăng tỷ lệ thành công của các dự án ứng dụng AI
- Cung cấp dữ liệu thực nghiệm phục vụ 3 câu hỏi nghiên cứu (Research Questions):
  - **RQ1**: Cải thiện độ chính xác trong việc ghép nối chuyên gia AI với yêu cầu khách hàng
  - **RQ2**: Hiệu quả của công cụ AI trong việc hỗ trợ người dùng không chuyên xác định phạm vi dự án
  - **RQ3**: Các yếu tố ảnh hưởng đến độ tin cậy và thành công của giao dịch trên marketplace dịch vụ AI

## 3. Tác nhân hệ thống (Actors)

| Actor | Bao gồm | Chức năng chính |
|---|---|---|
| **Client** | Doanh nghiệp, Startup, Nhà sáng lập | Đăng Job, thuê chuyên gia, quản lý dự án, thanh toán Escrow, đánh giá |
| **AI Expert** | Freelancer AI, AI Engineer, AI Consultant | Tạo hồ sơ/portfolio, đăng dịch vụ AI, gửi Proposal, thực hiện dự án, rút tiền |
| **Administrator** | Quản trị viên hệ thống | Quản lý người dùng/Job, xử lý tranh chấp, theo dõi giao dịch & phân tích dữ liệu |

## 4. Kiến trúc hệ thống

```text
Client (React SPA)
    ↓  REST + WebSocket/STOMP
Controller Layer
    ↓
Service Layer  ──→  AI Gateway (OpenAI / Gemini / Ollama)
    ↓
Repository Layer (Spring Data JPA)
    ↓
SQL Server 2022
```

Kiến trúc phân lớp (Layered Architecture) theo từng module nghiệp vụ (package-by-feature): mỗi module có đầy đủ `controller / dto / entity / repository / service / exception / mapper` riêng.

## 5. Công nghệ sử dụng

### Backend
- **Java 21**, **Spring Boot 3.5.14**, Maven
- Spring Data JPA + Hibernate
- Spring Security + JWT (jjwt) — access token + refresh token, rate limiting, khóa tài khoản sau nhiều lần đăng nhập sai
- Spring WebSocket + STOMP (chat thời gian thực, xác thực qua JWT ngay tại bước handshake)
- Spring Mail (thông báo qua email)
- Springdoc OpenAPI (Swagger UI)
- SQL Server 2022 (driver `mssql-jdbc`)
- AI Gateway đa nhà cung cấp: **OpenAI / Gemini / Ollama** (cấu hình qua `.env`, mặc định Gemini)
- Docker (multi-stage build, non-root user, healthcheck) + Docker Compose
- JUnit 5 + Mockito + Spring Boot Test (`@WebMvcTest`) + Spring Security Test

### Frontend
- **React 19** + **Vite 8**
- TailwindCSS 3.4
- TanStack React Query 5 (data fetching/caching)
- Zustand 5 (state management, persist vào localStorage)
- React Router 7
- Axios (kèm interceptor tự động refresh access token)
- STOMP.js 7 + SockJS-client (chat thời gian thực)
- react-hot-toast, lucide-react

## 6. Cấu trúc dự án

### Backend (`com.aitasker`)
```text
admin | ai | analytics | audit | auth | common | config | delivery
dispute | exception | expert | job | message | milestone | notification
payment | project | proposal | recommendation | review | security
user | websocket
```
Mỗi package nghiệp vụ đều tự chứa `controller/`, `dto/`, `entity/`, `repository/`, `service/` (và `exception/`, `mapper/` khi cần).

### Frontend (`frontend-aitasker/src`)
```text
api/         # 1 file gọi API tương ứng mỗi module Backend
components/  # UI dùng chung (layout, ui primitives)
hooks/       # useChatSocket (WebSocket/STOMP)...
layouts/     # Layout theo vai trò (Client/Expert/Admin)
pages/       # public | client | expert | admin | shared
router/      # ProtectedRoute, RoleRoute, GuestRoute
store/       # authStore (Zustand)
utils/       # statusMaps, formatters...
```

## 7. Danh sách API theo module

| Module | Base path | Ghi chú |
|---|---|---|
| Auth | `/api/auth` | register, login, forgot/reset-password, token refresh/revoke/verify |
| User | `/api/users` | thông tin cá nhân |
| Job Marketplace | `/api/jobs` | CRUD, search, phân trang |
| Proposal | `/api/proposals` | gửi/chấp nhận/từ chối/rút |
| Expert Profile | `/api/experts` | hồ sơ, tìm kiếm chuyên gia |
| Portfolio | `/api/experts/portfolio` | CRUD portfolio |
| Service Marketplace | `/api/services` | CRUD dịch vụ AI |
| Project | `/api/projects` | vòng đời dự án |
| Milestone | `/api/milestones` | tạo/nộp/duyệt/từ chối/giải ngân |
| Delivery | `/api/deliveries` | bàn giao sản phẩm |
| Payment / Escrow | `/api/payments` | deposit, release, lịch sử giao dịch |
| Withdrawal | `/api/withdrawals` | yêu cầu rút tiền (có kiểm tra số dư) |
| Dispute | `/api/disputes` | tạo, bằng chứng, nhắn tin, giải quyết |
| Review | `/api/reviews` | đánh giá 2 chiều |
| Message | `/api/messages` | lịch sử chat (realtime qua STOMP `/chat.send`) |
| Notification | `/api/notifications` | thông báo hệ thống |
| Email | `/api/email` | test gửi email |
| File Storage | `/api/files` | upload/download/xóa (gắn `projectId` để phân quyền) |
| AI Assistant | `/api/ai` | Job Assistant, Service Generator |
| Recommendation | `/api` | AI Expert Recommendation, Feedback |
| Admin – Users | `/api/admin/users` | tìm kiếm, lọc, phân trang, ban/unban |
| Admin – Jobs | `/api/admin/jobs` | phân trang, xóa |
| Admin – Payments | `/api/admin/payments` | giao dịch, rút tiền, duyệt withdrawal |
| Admin – Analytics | `/api/admin` | dashboard, analytics, reports |
| Audit Log | `/api/admin/audit-logs` | nhật ký hệ thống |

Tài liệu API đầy đủ (request/response, mã lỗi): Swagger UI tại `/swagger-ui/index.html` khi chạy Backend.

## 8. Mô-đun AI

| Mô-đun | Chức năng | Endpoint |
|---|---|---|
| AI Job Assistant | Sinh tiêu đề, mô tả, kỹ năng, ngân sách đề xuất | `POST /api/ai/job-assistant` |
| AI Service Generator | Sinh mô tả dịch vụ, tags, giá đề xuất, thời gian bàn giao | `POST /api/ai/service-generator` |
| AI Expert Recommendation | Đề xuất chuyên gia theo kỹ năng, rating, tỷ lệ hoàn thành dự án | `GET /api/ai/recommend-experts/{jobId}` |
| Recommendation Feedback | Đo hiệu quả đề xuất (được thuê hay không) phục vụ RQ1 | `POST/GET /api/recommendations/feedback` |

AI được gọi qua **AiGateway** trừu tượng hóa nhà cung cấp (không hardcode 1 provider); nếu `AI_ENABLED=false` hoặc chưa cấu hình API Key, hệ thống trả lỗi rõ ràng (503) thay vì dữ liệu giả.

## 9. Bảo mật & Hạ tầng

- JWT (access + refresh token), rate limiting theo IP, khóa đăng nhập sau nhiều lần sai
- Phân quyền theo Role (`CLIENT` / `EXPERT` / `ADMIN`) ở cả tầng URL (`SecurityConfig`) lẫn method (`@PreAuthorize`)
- Kiểm tra quyền sở hữu tài nguyên (chống IDOR) cho Job, File, Project
- Audit Log cho hành động đăng nhập, thanh toán, tranh chấp, thao tác Admin
- Ghi nhận sự kiện phân tích (`AnalyticsEvent`) xuyên suốt nghiệp vụ, phục vụ Dashboard và 3 câu hỏi nghiên cứu
- Docker multi-stage, non-root user, healthcheck; cấu hình `application-prod.yml` riêng (tắt `show-sql`, `ddl-auto: validate`, giới hạn Actuator endpoint)

## 10. Kiểm thử tự động

- **Unit test** (Mockito thuần): `EscrowServiceTest`, `DisputeServiceImplTest`, `JobServiceTest` — bao phủ logic nghiệp vụ trọng yếu (kiểm tra số dư rút tiền, quyền tham gia Dispute, IDOR)
- **Web-layer test** (`@WebMvcTest` + `@EnableMethodSecurity`, không cần DB thật): `AuthControllerTest`, `JobControllerTest`, `DisputeControllerTest`, `AdminUserControllerTest`, `FileStorageControllerTest`, `AiAssistantControllerTest`
- Chạy bằng `./mvnw test`, không phụ thuộc SQL Server hay biến môi trường thật

## 11. Hướng dẫn cài đặt & chạy

### Yêu cầu
- Java 21, Maven (hoặc dùng `./mvnw`)
- SQL Server 2022 (local hoặc Docker)
- Node.js 18+ (Frontend)

### Backend
```bash
cd aitasker
cp .env.example .env        # điền DB_PASSWORD, JWT_SECRET, AI_GEMINI_API_KEY...
./mvnw spring-boot:run
```

Biến môi trường chính (`.env`):

| Biến | Mô tả |
|---|---|
| `DB_HOST/PORT/NAME/USERNAME/PASSWORD` | Kết nối SQL Server |
| `JWT_SECRET` | Khóa ký JWT, tối thiểu 32 ký tự, **không có giá trị mặc định** |
| `AI_ENABLED`, `AI_PROVIDER`, `AI_GEMINI_API_KEY` | Bật/tắt và cấu hình AI Provider |
| `MAIL_HOST/PORT/USERNAME/PASSWORD` | SMTP gửi email (quên mật khẩu, thông báo) |
| `FILE_UPLOAD_DIR` | Thư mục lưu file upload |

Chạy bằng Docker: `docker compose up` (dùng `docker-compose.yml` + `Dockerfile` ở thư mục gốc `aitasker/`).

### Frontend
```bash
cd frontend-aitasker
cp .env.example .env         # VITE_API_BASE_URL, VITE_WS_URL
npm install
npm run dev
```

## 12. Trạng thái hoàn thiện (Phase 5)

Backend đã đáp ứng **~95% yêu cầu chức năng cốt lõi**, bao phủ đầy đủ 3 nhóm tác nhân và toàn bộ 3 mô-đun AI; cả 3 câu hỏi nghiên cứu đều có dữ liệu thực tế để phân tích (qua Recommendation Feedback, Analytics Event `AI_PROMPT_USED`, và Escrow/Milestone/Review/Dispute Event). Frontend đã được kiểm tra và đồng bộ tương thích với Backend (endpoint, DTO, phân trang, WebSocket).

## 13. Quy trình phát triển

### Branch Strategy
```text
main
└── develop
    ├── feature-auth
    ├── feature-user
    ├── feature-job
    ├── feature-project
    ├── feature-payment
    └── feature-chat
```

### Commit Convention
```bash
feat: create login api
fix: resolve jwt validation bug
refactor: optimize project service
docs: update README
```

Quy trình phát triển: Requirement Analysis → System Design → Technical Foundation Setup → Backend Development → Frontend Development → Testing & Integration → Bug Fixing & Compatibility Review.

## 14. Định hướng phát triển tiếp theo

- AI-powered dispute resolution
- AI project risk prediction
- Tối ưu hóa Recommendation Engine
- Dashboard analytics chuyên sâu hơn
- Triển khai Cloud (AWS/GCP)
- Ứng dụng Mobile

## 15. Nhóm phát triển

**Dự án**: AITasker
**Môn học**: Java Programming Project
**Hình thức**: Research-Based Learning

## 16. Giấy phép

Dự án được phát triển phục vụ mục đích học tập và nghiên cứu.
