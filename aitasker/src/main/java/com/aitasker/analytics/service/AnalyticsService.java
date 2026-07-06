package com.aitasker.analytics.service;

import com.aitasker.analytics.entity.AnalyticsEvent;
import com.aitasker.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsEventRepository analyticsEventRepository;

    public void record(String eventType, Long refId, String metadata) {
        analyticsEventRepository.save(
                AnalyticsEvent.builder()
                        .eventType(eventType)
                        .refId(refId)
                        .metadata(metadata)
                        .build()
        );
    }

    public Map<String, Long> countByEventTypes(List<String> types) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (String type : types) {
            result.put(type, analyticsEventRepository.countByEventType(type));
        }
        return result;
    }
}
