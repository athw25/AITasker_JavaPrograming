# AITasker - Tài khoản Demo (Demo Accounts)

Tài liệu này mô tả toàn bộ các tài khoản mẫu được tạo tự động bởi `DemoDataSeeder.java` để phục vụ việc kiểm thử (Testing), trình diễn (Demo) và phát triển hệ thống AITasker.

---

# 1. Điều kiện tạo dữ liệu Demo

Dữ liệu demo sẽ được tạo tự động khi ứng dụng Spring Boot khởi động nếu thỏa mãn các điều kiện sau:

- `app.seed.demo-data=true`
- Trong cơ sở dữ liệu chưa tồn tại tài khoản:

```text
client1@aitasker.com
```

Nếu tài khoản này đã tồn tại thì Seeder sẽ **bỏ qua toàn bộ quá trình tạo dữ liệu** nhằm tránh tạo dữ liệu trùng lặp.

---

# 2. Tài khoản Quản trị viên (Administrator)

Hệ thống tạo sẵn một tài khoản quản trị có toàn quyền truy cập.

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | ADMIN |
| Họ tên | System Admin |
| Email | admin@aitasker.com |
| Mật khẩu | Admin@123 |
| Trạng thái | ACTIVE |

## Chức năng

Tài khoản này dùng để:

- Quản lý người dùng
- Quản lý dự án
- Quản lý tranh chấp
- Quản lý thanh toán
- Xem Audit Log
- Quản trị toàn bộ hệ thống

---

# 3. Tài khoản Khách hàng (Client)

Hệ thống tạo sẵn 3 khách hàng.

## Client 1

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | CLIENT |
| Họ tên | Nguyen Van Client |
| Email | client1@aitasker.com |
| Mật khẩu | Demo@123 |
| Trạng thái | ACTIVE |

---

## Client 2

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | CLIENT |
| Họ tên | Tran Thi Client |
| Email | client2@aitasker.com |
| Mật khẩu | Demo@123 |
| Trạng thái | ACTIVE |

---

## Client 3

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | CLIENT |
| Họ tên | Le Hoang Client |
| Email | client3@aitasker.com |
| Mật khẩu | Demo@123 |
| Trạng thái | ACTIVE |

---

## Quyền của Client

Client có thể:

- Đăng ký / Đăng nhập
- Tạo Job
- Chỉnh sửa Job
- Xem Proposal
- Chấp nhận Proposal
- Tạo Project
- Chat với Expert
- Thanh toán Escrow
- Đánh giá Expert
- Tạo Dispute
- Upload tài liệu

---

# 4. Tài khoản Chuyên gia (Expert)

Hệ thống tạo sẵn 3 chuyên gia AI.

## Expert 1

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | EXPERT |
| Họ tên | Pham Minh Expert |
| Email | expert1@aitasker.com |
| Mật khẩu | Demo@123 |
| Trạng thái | ACTIVE |

### Hồ sơ

- Chức danh: Senior AI Engineer
- Kinh nghiệm: 6 năm
- Rate: 45 USD/giờ

### Kỹ năng

- Python
- PyTorch
- LLM
- LangChain

---

## Expert 2

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | EXPERT |
| Họ tên | Vo Thi Expert |
| Email | expert2@aitasker.com |
| Mật khẩu | Demo@123 |
| Trạng thái | ACTIVE |

### Hồ sơ

- Chức danh: NLP Specialist
- Kinh nghiệm: 4 năm
- Rate: 35 USD/giờ

### Kỹ năng

- Python
- spaCy
- Transformers
- NLP

---

## Expert 3

| Thuộc tính | Giá trị |
|------------|----------|
| Vai trò | EXPERT |
| Họ tên | Dang Quoc Expert |
| Email | expert3@aitasker.com |
| Mật khẩu | Demo@123 |
| Trạng thái | ACTIVE |

### Hồ sơ

- Chức danh: Computer Vision Engineer
- Kinh nghiệm: 8 năm
- Rate: 60 USD/giờ

### Kỹ năng

- Python
- OpenCV
- TensorFlow
- Computer Vision

---

# 5. Danh sách đăng nhập nhanh

## Administrator

```text
Email:
admin@aitasker.com

Password:
Admin@123
```

---

## Clients

### Client 1

```text
Email:
client1@aitasker.com

Password:
Demo@123
```

### Client 2

```text
Email:
client2@aitasker.com

Password:
Demo@123
```

### Client 3

```text
Email:
client3@aitasker.com

Password:
Demo@123
```

---

## Experts

### Expert 1

```text
Email:
expert1@aitasker.com

Password:
Demo@123
```

### Expert 2

```text
Email:
expert2@aitasker.com

Password:
Demo@123
```

### Expert 3

```text
Email:
expert3@aitasker.com

Password:
Demo@123
```

---

# 6. Mật khẩu mặc định

| Vai trò | Mật khẩu |
|----------|-----------|
| ADMIN | Admin@123 |
| CLIENT | Demo@123 |
| EXPERT | Demo@123 |

---

# 7. Tóm tắt

| Vai trò | Số lượng | Email | Password |
|----------|----------|--------|----------|
| ADMIN | 1 | admin@aitasker.com | Admin@123 |
| CLIENT | 3 | client1@aitasker.com | Demo@123 |
| CLIENT | 3 | client2@aitasker.com | Demo@123 |
| CLIENT | 3 | client3@aitasker.com | Demo@123 |
| EXPERT | 3 | expert1@aitasker.com | Demo@123 |
| EXPERT | 3 | expert2@aitasker.com | Demo@123 |
| EXPERT | 3 | expert3@aitasker.com | Demo@123 |

---

# 8. Lưu ý

- Dữ liệu demo chỉ được tạo khi `app.seed.demo-data=true`.
- Seeder chỉ chạy khi chưa tồn tại tài khoản `client1@aitasker.com`.
- Seeder được thiết kế theo cơ chế **Idempotent**, nghĩa là có thể khởi động ứng dụng nhiều lần mà không tạo dữ liệu trùng lặp.
- Tất cả tài khoản đều ở trạng thái **ACTIVE** ngay sau khi được tạo.
- Đây là các tài khoản dành cho mục đích phát triển, kiểm thử và trình diễn hệ thống, không sử dụng trong môi trường Production.