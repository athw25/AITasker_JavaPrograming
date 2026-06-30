package com.aitasker.common.enums;

/**
 * Trạng thái của Milestone.
 *
 * Vòng đời thông thường:
 *
 * PENDING
 * ↓
 * SUBMITTED
 * ↓
 * APPROVED
 * ↓
 * PAID
 *
 * Hoặc:
 *
 * SUBMITTED
 * ↓
 * REJECTED
 * ↓
 * SUBMITTED
 */
public enum MilestoneStatus {

    /**
     * Milestone mới được tạo,
     * Expert chưa nộp sản phẩm.
     */
    PENDING,

    /**
     * Expert đã nộp sản phẩm.
     */
    SUBMITTED,

    /**
     * Client đã phê duyệt sản phẩm.
     */
    APPROVED,

    /**
     * Client từ chối sản phẩm,
     * Expert cần nộp lại.
     */
    REJECTED,

    /**
     * Milestone đã được thanh toán.
     */
    PAID
}