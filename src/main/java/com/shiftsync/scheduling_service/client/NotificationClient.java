package com.shiftsync.scheduling_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationClient {
    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void logSwapApproved(Long swapRequestId, Long shiftId, Long approvedEmployeeId) {
        try {
            Map<String, Object> body = Map.of(
                    "eventType", "SWAP_APPROVED",
                    "actorId", String.valueOf(approvedEmployeeId),
                    "shiftId", String.valueOf(shiftId),
                    "metadata", Map.of("swapRequestId", swapRequestId)
            );
            restTemplate.postForObject("http://notification-service/events", body, Object.class);
        } catch (Exception e) {
            // Deliberately swallow the error — a notification failure must never break the swap approval itself
            log.warn("Failed to notify notification-service of swap approval for swapRequestId={}: {}",
                    swapRequestId, e.getMessage());
        }
    }

    public void logSwapRejected(Long swapRequestId, Long shiftId, Long requestingEmployeeId) {
        try {
            Map<String, Object> body = Map.of(
                    "eventType", "SWAP_REJECTED",
                    "actorId", String.valueOf(requestingEmployeeId),
                    "shiftId", String.valueOf(shiftId),
                    "metadata", Map.of("swapRequestId", swapRequestId)
            );
            restTemplate.postForObject("http://notification-service/events", body, Object.class);
        } catch (Exception e) {
            log.warn("Failed to notify notification-service of swap rejection for swapRequestId={}: {}",
                    swapRequestId, e.getMessage());
        }
    }
}
