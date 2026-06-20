package com.aitasker.commontest;

import com.aitasker.common.response.PageResponse;

import java.util.List;

public class TestPageResponse {

    public static void main(String[] args) {

        PageResponse<String> page = new PageResponse<>();

        page.setContent(List.of("Java", "Spring Boot"));
        page.setCurrentPage(0);
        page.setPageSize(10);
        page.setTotalElements(25);
        page.setTotalPages(3);
        page.setFirst(true);
        page.setLast(false);

        System.out.println(page.getContent());
        System.out.println(page.getCurrentPage());
        System.out.println(page.getPageSize());
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.isFirst());
        System.out.println(page.isLast());
    }
}