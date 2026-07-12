package com.aitasker.ai.util;

public final class AiJsonExtractor {

    private AiJsonExtractor() {
    }

    // Một số AI Provider bọc JSON trong ```json ... ``` hoặc kèm text thừa,
    // hàm này chỉ giữ lại phần từ dấu { đầu tiên đến dấu } cuối cùng.
    public static String extractJsonObject(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start == -1 || end == -1 || end < start) {
            return raw.trim();
        }
        return raw.substring(start, end + 1);
    }
}
