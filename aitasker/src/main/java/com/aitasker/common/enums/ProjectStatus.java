package com.aitasker.common.enums;

/**
 * Trạng thái của Project trong hệ thống AITasker.
 *
 * Vòng đời:
 *
 * ACTIVE
 * ↓
 * COMPLETED
 *
 * ACTIVE
 * ↓
 * CANCELLED
 *
 * ACTIVE
 * ↓
 * DISPUTED
 */
public enum ProjectStatus {

    /**
     * Dự án đang được thực hiện.
     */
    ACTIVE,

    /**
     * Dự án đã hoàn thành.
     */
    COMPLETED,

    /**
     * Dự án bị hủy.
     */
    CANCELLED,

    /**
     * Dự án đang có tranh chấp.
     */
    DISPUTED
}