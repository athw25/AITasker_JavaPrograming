package com.aitasker.common.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chuẩn response trả về cho tất cả API trong hệ thống AITasker.
 *
 * <p>Sử dụng static factory methods để tạo response:
 * <ul>
 *   <li>{@code ApiResponse.success(data)} — thành công với dữ liệu, message mặc định</li>
 *   <li>{@code ApiResponse.success(message, data)} — thành công với dữ liệu và message tùy chỉnh</li>
 *   <li>{@code ApiResponse.error(message)} — lỗi không có dữ liệu</li>
 *   <li>{@code ApiResponse.error(message, data)} — lỗi kèm dữ liệu bổ sung</li>
 * </ul>
 *
 * @param <T> Kiểu dữ liệu trả về
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

    /** Trạng thái: true = thành công, false = lỗi */
    private boolean success;

    /** Thông điệp mô tả kết quả */
    private String message;

    /** Dữ liệu trả về (null nếu lỗi hoặc không có dữ liệu) */
    private T data;

    /** Thời điểm tạo response (UTC) */
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // ─── Static factory: SUCCESS ─────────────────────────────────────────────

    /**
     * Tạo response thành công với dữ liệu, message mặc định "Success".
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    /**
     * Tạo response thành công với message tùy chỉnh và dữ liệu.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Tạo response thành công không kèm dữ liệu (chỉ thông báo).
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // ─── Static factory: ERROR ────────────────────────────────────────────────

    /**
     * Tạo response lỗi với message, không kèm dữ liệu.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Tạo response lỗi với message và dữ liệu bổ sung (ví dụ: validation errors).
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    /**
     * Tạo response thất bại với message (alias cho error).
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Tạo response thất bại với message và dữ liệu bổ sung.
     */
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
