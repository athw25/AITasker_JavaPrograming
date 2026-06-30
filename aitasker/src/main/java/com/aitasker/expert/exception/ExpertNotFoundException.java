package com.aitasker.expert.exception;

/**
 * Ngoại lệ ném ra khi không tìm thấy thông tin Chuyên gia trong hệ thống.
 */
public class ExpertNotFoundException extends RuntimeException {
    public ExpertNotFoundException(String message) {
        super(message);
    }
}