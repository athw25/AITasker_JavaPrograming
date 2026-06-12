package com.aitasker.common.util; // Đảm bảo trùng với cấu trúc thư mục của bạn hiện tại

import java.util.regex.Pattern;

/**
 * Thư viện tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào (Validation) toàn hệ thống.
 */
public final class ValidationUtils {

    // Không cho phép khởi tạo đối tượng ValidationUtils từ bên ngoài
    private ValidationUtils() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích, không thể khởi tạo!");
    }

    // Biểu thức chính quy (Regex) để kiểm tra định dạng Email chuẩn
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    // Biểu thức chính quy kiểm tra Số điện thoại Việt Nam (Đầu số 03, 05, 07, 08, 09, độ dài 10 số)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})$");

    // ================= 1. KIỂM TRA CHUỖI RỖNG (EMPTY / NULL) =================

    /**
     * Kiểm tra một chuỗi có bị null hoặc rỗng (chỉ toàn khoảng trắng) hay không.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // ================= 2. KIỂM TRA ĐỊNH DẠNG (EMAIL / PHONE / URL) =================

    /**
     * Kiểm tra định dạng Email của tài khoản Chuyên gia hoặc Doanh nghiệp.
     */
    public static boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Kiểm tra định dạng Số điện thoại liên hệ.
     */
    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Kiểm tra ngân sách (Budget) dự án doanh nghiệp đưa ra có phải là số dương hợp lệ không.
     */
    public static boolean isPositiveNumber(Double number) {
        return number != null && number > 0;
    }
}