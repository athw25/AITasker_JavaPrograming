// GlobalExceptionHandler.java
package com.aitasker.exception;
import com.aitasker.common.response.BaseResponse;
import com.aitasker.expert.exception.ExpertNotFoundException;
import com.aitasker.expert.exception.ServicePackageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // không tìm thấy dữ liệu (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse> handleResourceNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, ex.getMessage()));
    }

    // Hai exception này trước đây KHÔNG có handler riêng nên rơi vào nhánh
    // 500 chung dù bản chất là lỗi 404 (không tìm thấy expert/service package).
    @ExceptionHandler(ExpertNotFoundException.class)
    public ResponseEntity<BaseResponse> handleExpertNotFound(ExpertNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(ServicePackageNotFoundException.class)
    public ResponseEntity<BaseResponse> handleServicePackageNotFound(ServicePackageNotFoundException ex){
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

    // Spring Security ném AccessDeniedException (kể cả khi bị throw thủ công
    // trong service, ví dụ NotificationService.markAsRead) — trước đây không
    // có handler riêng nên cũng rơi vào nhánh 500 chung thay vì 403 đúng nghĩa.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse> handleAccessDenied(AccessDeniedException ex){
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(new BaseResponse(false, ex.getMessage()));
    }

    //Lỗi IllegalArgumentException - thường dùng cho các lỗi liên quan đến dữ liệu đầu vào không hợp lệ, hoặc lỗi logic trong code.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                new BaseResponse(false, ex.getMessage())
        );
    }

    // Lỗi hệ thống chung (500).
    // QUAN TRỌNG: phải log đầy đủ stacktrace ở đây — trước bản vá, handler
    // này nuốt hoàn toàn exception (không log gì), nên mọi lỗi 500 thật ở
    // production đều KHÔNG để lại dấu vết gì để debug. Đây luôn phải là
    // @ExceptionHandler cuối cùng (bắt Exception chung) để không che mất các
    // handler cụ thể hơn ở trên.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception ex){
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(false, "Internal Server Error"));
    }
}
