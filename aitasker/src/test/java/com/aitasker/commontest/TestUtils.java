package com.aitasker.commontest;

import com.aitasker.common.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestUtils {

    public static void main(String[] args) {

        System.out.println("formatDate:");
        System.out.println(
                DateUtils.formatDate(
                        LocalDate.of(2026, 6, 12)
                )
        );

        System.out.println("\nformatDateTime:");
        System.out.println(
                DateUtils.formatDateTime(
                        LocalDateTime.of(2026, 6, 12, 10, 20, 30)
                )
        );

        System.out.println("\nparseDate:");
        System.out.println(
                DateUtils.parseDate("12/06/2026")
        );

        System.out.println(
                DateUtils.parseDate("abc")
        );

        System.out.println("\nisExpired:");
        System.out.println(
                DateUtils.isExpired(LocalDate.now().minusDays(1))
        );

        System.out.println(
                DateUtils.isExpired(LocalDate.now().plusDays(1))
        );

        System.out.println("\ngetDaysBetween:");
        System.out.println(
                DateUtils.getDaysBetween(
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 6, 11)
                )
        );
    }
}