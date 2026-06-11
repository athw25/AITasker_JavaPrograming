package utils; // Đảm bảo trùng với cấu trúc thư mục của bạn hiện tại

import constants.AppConstants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Thư viện tiện ích xử lý và định dạng ngày tháng toàn hệ thống.
 */
public final class DateUtils {

    // Không cho phép khởi tạo đối tượng DateUtils từ bên ngoài
    private DateUtils() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích, không thể khởi tạo!");
    }

    // Khởi tạo sẵn bộ định dạng lấy từ AppConstants để tối ưu hiệu năng
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(AppConstants.DATETIME_FORMAT);

    // ================= 1. ĐỊNH DẠNG THỜI GIAN (FORMAT) =================

    /**
     * Chuyển từ LocalDate sang Chuỗi (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    /**
     * Chuyển từ LocalDateTime sang Chuỗi (dd/MM/yyyy HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATETIME_FORMATTER);
    }

    // ================= 2. CHUYỂN ĐỔI CHUỖI THÀNH THỜI GIAN (PARSE) =================

    /**
     * Chuyển chuỗi chữ (dd/MM/yyyy) thành đối tượng LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null; // Trả về null nếu chuỗi truyền vào sai định dạng
        }
    }

    // ================= 3. LOGIC TÍNH TOÁN THỜI GIAN =================

    /**
     * Tính khoảng cách số ngày giữa hai mốc thời gian.
     * Ứng dụng: Tính số ngày còn lại của một dự án AI trước khi hết hạn nhận hồ sơ.
     */
    public static long getDaysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Kiểm tra xem một mốc thời gian đã quá hạn (hết hạn) so với hiện tại chưa.
     * Ứng dụng: Kiểm tra bài đăng tìm chuyên gia của Doanh nghiệp đã hết hạn chưa.
     */
    public static boolean isExpired(LocalDate expiryDate) {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }
}