
package com.aitasker.common.response;

import java.time.LocalDateTime;

/**
 * Cấu trúc phản hồi API chuẩn hóa toàn hệ thống.
 * @param <T> Kiểu dữ liệu của dữ liệu phản hồi (data)
 */
public class ApiResponse<T> {
    private int statusCode;          // Mã trạng thái HTTP (Ví dụ: 200, 201, 400, 404, 500)
    private String message;          // Thông báo chi tiết (Ví dụ: "Thành công", "Không tìm thấy chuyên gia")
    private T data;                  // Dữ liệu thực tế trả về cho Frontend
    private LocalDateTime timestamp; // Thời gian trả phản hồi (để log và debug)

    // ================= CONSTRUCTORS =================

    /**
     * Khởi tạo phản hồi không kèm dữ liệu (thường dùng cho thông báo lỗi hoặc xóa/cập nhật)
     */
    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Khởi tạo phản hồi đầy đủ thông tin (thường dùng khi truy vấn dữ liệu thành công)
     */
    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // ================= STATIC FACTORY METHODS =================
    // Các hàm tạo nhanh giúp viết code ở Controller ngắn gọn hơn

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message);
    }

    // ================= GETTERS & SETTERS =================

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}