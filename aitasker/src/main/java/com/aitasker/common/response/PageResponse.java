package com.aitasker.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Cấu trúc dữ liệu trả về phục vụ tính năng phân trang hệ thống.
 *
 * @param <T> Kiểu dữ liệu của các phần tử trong danh sách
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * Danh sách dữ liệu của trang hiện tại
     */
    private List<T> content;

    /**
     * Trang hiện tại (Spring bắt đầu từ 0)
     */
    private int currentPage;

    /**
     * Số phần tử trên mỗi trang
     */
    private int pageSize;

    /**
     * Tổng số bản ghi trong database
     */
    private long totalElements;

    /**
     * Tổng số trang
     */
    private int totalPages;

    /**
     * Có phải trang đầu tiên không
     */
    private boolean first;

    /**
     * Có phải trang cuối cùng không
     */
    private boolean last;
}