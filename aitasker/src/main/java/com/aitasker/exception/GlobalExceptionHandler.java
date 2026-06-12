// GlobalExceptionHandler.java
package com.aitasker.exception;
import com.aitasker.common.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // không tìm thấy dữ liệu (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> handleResourceNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, ex.getMessage()));
    }
    // dữ liệu đầu vào không hợp lệ(400)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse> handleBadRequest(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(false, ex.getMessage()));
    }
    // chưa đăng nhập hoặc token sai (401)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse> handleUnauthorized(UnauthorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(false, ex.getMessage()));
    }
    // không có quyền truy cập (403)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<BaseResponse> handleForbidden(ForbiddenException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BaseResponse(false, ex.getMessage()));
    }
    //lỗi nghiệp vụ(422)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse> handleBusiness(BusinessException ex){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new BaseResponse(false, ex.getMessage()));
    }

    // Lỗi validation DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    //Lỗi hệ thống chung(500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, "Internal Server Error"));
    }

    //Lỗi IllegalArgumentException - thường dùng cho các lỗi liên quan đến dữ liệu đầu vào không hợp lệ, hoặc lỗi logic trong code.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        return ResponseEntity.badRequest().body(
                new BaseResponse(false, ex.getMessage())
        );
    }
}