package constants; // Đảm bảo trùng với cấu trúc thư mục của bạn hiện tại

/**
 * Định nghĩa tất cả các hằng số dùng chung cho toàn bộ hệ thống.
 */
public final class AppConstants {

    // Không cho phép khởi tạo đối tượng AppConstants từ bên ngoài
    private AppConstants() {
        throw new UnsupportedOperationException("Đây là lớp hằng số, không thể khởi tạo!");
    }

    // ================= 1. CẤU HÌNH PHÂN TRANG MẶC ĐỊNH =================
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // ================= 2. ĐỊNH DẠNG THỜI GIAN =================
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    // ================= 3. VAI TRÒ NGƯỜI DÙNG (ROLES) =================
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ENTERPRISE = "ENTERPRISE"; // Doanh nghiệp cần AI
    public static final String ROLE_EXPERT = "EXPERT";         // Chuyên gia AI

    // ================= 4. TRẠNG THÁI DỰ ÁN TỰ ĐỘNG HÓA AI =================
    public static final String PROJECT_STATUS_PENDING = "PENDING";     // Doanh nghiệp mới đăng bài, chờ duyệt
    public static final String PROJECT_STATUS_APPROVED = "APPROVED";   // Đã duyệt, đang tìm chuyên gia
    public static final String PROJECT_STATUS_IN_PROGRESS = "IN_PROGRESS"; // Đã kết nối chuyên gia và đang làm
    public static final String PROJECT_STATUS_COMPLETED = "COMPLETED"; // Dự án hoàn thành
    public static final String PROJECT_STATUS_CANCELLED = "CANCELLED"; // Dự án bị hủy

    // ================= 5. TRẠNG THÁI LỊCH PHỎNG VẤN/KẾT NỐI =================
    public static final String MATCH_STATUS_REQUESTED = "REQUESTED";   // Chuyên gia ứng tuyển hoặc DN mời
    public static final String MATCH_STATUS_ACCEPTED = "ACCEPTED";     // Hai bên đồng ý kết nối
    public static final String MATCH_STATUS_REJECTED = "REJECTED";     // Từ chối kết nối
}