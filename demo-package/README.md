# AITasker — Demo Seed Data & API Test Suite

Bộ công cụ demo dữ liệu mẫu + test API tự động cho Backend AITasker, KHÔNG thay đổi bất kỳ business logic nào hiện có.

## Cấu trúc thư mục

```
/seed            -> DemoDataSeeder.java (copy vào backend, chạy tự động lúc start)
/demo-data        -> (file mẫu được Seeder tự sinh vào thư mục uploads/ lúc chạy)
/postman          -> Postman Collection + Environment (83 request, phủ toàn bộ API)
/bruno            -> Bruno Collection (luồng nghiệp vụ chính)
/scripts          -> Auto Test (Node.js) + script reset dữ liệu (SQL)
```

## 1. Cài đặt Seeder

```bash
cp seed/DemoDataSeeder.java  <project>/aitasker/src/main/java/com/aitasker/config/DemoDataSeeder.java
```

Thêm dòng sau vào `application.yaml` (nếu chưa có, mặc định đã là `true`):
```yaml
app:
  seed:
    demo-data: true
```

Chạy backend bình thường:
```bash
./mvnw spring-boot:run
```

Seeder tự động chạy MỘT LẦN lúc khởi động, tạo toàn bộ dữ liệu mẫu. Nếu chạy lại (restart) mà dữ liệu demo đã tồn tại (`client1@aitasker.com` đã có), seeder tự bỏ qua — **an toàn khi restart nhiều lần, không tạo trùng**.

Muốn tắt seeder (ví dụ khi deploy thật): set `SEED_DEMO_DATA=false` hoặc `app.seed.demo-data=false`.

## 2. Tài khoản Demo

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | admin@aitasker.com | Admin@123 |
| Client 1/2/3 | client1@aitasker.com .. client3@aitasker.com | Demo@123 |
| Expert 1/2/3 | expert1@aitasker.com .. expert3@aitasker.com | Demo@123 |

## 3. Dữ liệu demo được tạo

- **Job**: 6 job với đủ trạng thái OPEN, IN_PROGRESS, CLOSED, CANCELLED
- **Proposal**: 8 proposal với đủ trạng thái PENDING, ACCEPTED, REJECTED, WITHDRAWN
- **Project**: 5 project với đủ trạng thái ACTIVE, COMPLETED, DISPUTED, CANCELLED
- **Milestone**: đủ trạng thái PENDING, SUBMITTED, APPROVED, REJECTED, PAID
- **Payment**: đủ trạng thái HELD, RELEASED, REFUNDED (kèm Transaction DEPOSIT/RELEASE/REFUND)
- **Withdrawal**: đủ trạng thái PENDING, APPROVED, REJECTED
- **Dispute**: 1 OPEN, 1 RESOLVED_REFUND
- **Review, Notification, Message, AuditLog, Attachment (PDF/DOCX/PNG/ZIP)**: đầy đủ

## 4. Import Postman

1. Mở Postman → Import → chọn `postman/AITasker.postman_collection.json` và `postman/AITasker.postman_environment.json`.
2. Chọn Environment "AITasker - Local" ở góc trên phải.
3. Chạy folder **01. Auth & Users** trước tiên để lấy token (script tự lưu vào biến môi trường).
4. Chạy các folder còn lại theo thứ tự số (02 → 13) — mỗi request tự dùng token/id đã lưu từ request trước.
5. Hoặc dùng **Collection Runner** để chạy toàn bộ collection tự động theo thứ tự.

## 5. Import Bruno

1. Mở Bruno → Open Collection → chọn thư mục `bruno/AITasker`.
2. Chạy lần lượt các thư mục `01-Auth` → `08-Admin`.

## 6. Chạy Auto Test (Node.js)

```bash
cd scripts
npm install
BASE_URL=http://localhost:8080/api npm test
```

Script tự động: đăng ký tài khoản mới (không đụng dữ liệu demo có sẵn) → login → chạy toàn bộ luồng Job → Proposal → Project → Milestone → Payment → Withdrawal → Review → Dispute → AI → Admin, in kết quả PASS/FAIL từng bước, thoát với exit code 0 nếu tất cả pass, 1 nếu có lỗi (dùng được trong CI/CD).

## 7. Reset dữ liệu

**Cách 1 — SQL (giữ nguyên schema):**
```bash
sqlcmd -S localhost -U sa -P <password> -i scripts/reset-demo-data.sql
```
Sau đó khởi động lại backend để Seeder tự chạy lại.

**Cách 2 — Reset toàn bộ (xóa cả schema):**
Xóa database `AITasker` trong SSMS, tạo lại database rỗng, khởi động lại backend (với `ddl-auto: update`) để Hibernate tự tạo schema + Seeder tự chạy.

## Lưu ý

- Bộ công cụ này CHỈ bổ sung dữ liệu và test, không sửa bất kỳ Controller/Service/Entity nghiệp vụ nào.
- File upload demo (PDF/DOCX/PNG/ZIP) được Seeder tự sinh trực tiếp vào thư mục `uploads/` cấu hình ở `app.upload.dir`, không cần chuẩn bị file thủ công.
- Bruno collection chỉ phủ luồng nghiệp vụ chính (không phủ 100% như Postman) — dùng Postman collection nếu cần test đầy đủ từng API riêng lẻ.
