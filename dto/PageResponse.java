package dto;

import java.util.List;

/**
 * Cấu trúc dữ liệu trả về phục vụ tính năng phân trang hệ thống.
 * @param <T> Kiểu dữ liệu của các phần tử trong danh sách
 */
public class PageResponse<T> {
    private List<T> content;        // Danh sách dữ liệu của trang hiện tại (Ví dụ: danh sách 10 chuyên gia)
    private int currentPage;        // Trang hiện tại (Hệ thống thường bắt đầu từ số 0)
    private int pageSize;           // Số lượng phần tử tối đa trên một trang (Ví dụ: 10, 20, 50)
    private long totalElements;     // Tổng số lượng bản ghi thực tế tồn tại trong Database
    private int totalPages;         // Tổng số trang tính được (totalElements / pageSize)

    // ================= CONSTRUCTOR =================

    public PageResponse(List<T> content, int currentPage, int pageSize, long totalElements, int totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    // ================= GETTERS & SETTERS =================

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}