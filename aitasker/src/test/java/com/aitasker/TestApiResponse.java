package com.aitasker;

import com.aitasker.common.response.ApiResponse;

public class TestApiResponse {

    public static void main(String[] args) {

        ApiResponse<String> response =
                ApiResponse.success("OK", "Hello");

        System.out.println("Success: " + response.isSuccess());
        System.out.println("Message: " + response.getMessage());
        System.out.println("Data: " + response.getData());
        System.out.println("Timestamp: " + response.getTimestamp());
    }
}