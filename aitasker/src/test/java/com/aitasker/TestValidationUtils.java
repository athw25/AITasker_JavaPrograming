package com.aitasker;

import com.aitasker.common.util.ValidationUtils;

public class TestValidationUtils {

    public static void main(String[] args) {

        System.out.println("=== isBlank ===");
        System.out.println(ValidationUtils.isBlank(null));
        System.out.println(ValidationUtils.isBlank(""));
        System.out.println(ValidationUtils.isBlank("   "));
        System.out.println(ValidationUtils.isBlank("Java"));

        System.out.println("\n=== Email ===");
        System.out.println(ValidationUtils.isValidEmail("abc@gmail.com"));
        System.out.println(ValidationUtils.isValidEmail("abc"));

        System.out.println("\n=== Phone ===");
        System.out.println(ValidationUtils.isValidPhone("0912345678"));
        System.out.println(ValidationUtils.isValidPhone("0388888888"));
        System.out.println(ValidationUtils.isValidPhone("123456"));

        System.out.println("\n=== Positive Number ===");
        System.out.println(ValidationUtils.isPositiveNumber(100.0));
        System.out.println(ValidationUtils.isPositiveNumber(0.0));
        System.out.println(ValidationUtils.isPositiveNumber(-5.0));
    }
}