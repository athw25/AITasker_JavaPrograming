package com.aitasker.common.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse<T> extends BaseResponse {

    private T data;

    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(
            boolean success,
            String message,
            T data
    ) {
        super(success, message);
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(
            String message
    ) {
        return new ApiResponse<>(false, message, null);
    }
}