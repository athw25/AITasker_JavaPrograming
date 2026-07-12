package com.aitasker.ai.util;

public final class AiJsonParser {

    private AiJsonParser() {
    }

    /**
     * Một số Provider (vd: Gemini) thường bọc JSON trong khối markdown ```json ... ```.
     * Hàm này bóc tách để lấy phần JSON thuần trước khi deserialize.
     */
    public static String extractJson(String rawResponse) {
        String text = rawResponse.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```[a-zA-Z]*", "").trim();
            int lastFence = text.lastIndexOf("```");
            if (lastFence >= 0) {
                text = text.substring(0, lastFence).trim();
            }
        }
        return text;
    }
}
