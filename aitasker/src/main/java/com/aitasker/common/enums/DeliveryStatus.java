package com.aitasker.common.enums;

/**
 * Trạng thái của Delivery.
 *
 * Một Delivery có thể:
 *
 * SUBMITTED
 * ↓
 * APPROVED
 *
 * hoặc:
 *
 * SUBMITTED
 * ↓
 * REJECTED
 */
public enum DeliveryStatus {

    /**
     * Expert đã nộp sản phẩm.
     */
    SUBMITTED,

    /**
     * Client đã phê duyệt sản phẩm.
     */
    APPROVED,

    /**
     * Client từ chối sản phẩm.
     */
    REJECTED
}