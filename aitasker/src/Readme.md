#Chạy chương trình BE: ..\AITasker_JavaPrograming\aitasker
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run

#Chạy FE: cd frontend-aitasker
npm install
npm run dev

#Tài khoản admin hiện tại:
Email:    admin@aitasker.com
Password: Admin@123

#Muốn đổi thông tin mặc định, thêm vào application.yaml:
app:
  admin:
    email: admin@aitasker.com
    password: MatKhauCuaBan
    
 // Lưu ý: seeder chỉ chạy nếu DB chưa có Admin nào. Nếu bạn đã lỡ tự đăng ký được 1 tài khoản ADMIN bằng lỗ hổng cũ trước khi vá, seeder sẽ không tạo thêm (vì hệ thống đã có Admin) — cứ dùng tài khoản đó, không ảnh hưởng gì.

 