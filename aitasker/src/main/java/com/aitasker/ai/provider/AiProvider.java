package com.aitasker.ai.provider;

public interface AiProvider {

    String getProviderName();

    boolean isAvailable();

    String call(String prompt);
}