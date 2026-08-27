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

    public void sendNotification(String userId, String message) {
        try {
            Map<String, Object> body = Map.of(
                    "userId", userId,
                    "message", message,
                    "channel", "IN_APP"
            );
            restTemplate.postForObject("http://notification-service/notifications", body, Object.class);
        } catch (Exception e) {
            log.warn("Failed to create notification for userId={}: {}", userId, e.getMessage());
        }
    }

    public void logSwapCreated(Long swapRequestId, Long shiftId,
                               Long requestingEmployeeId, String requestingEmployeeName,
                               Long targetEmployeeId, String targetEmployeeName) {
        send("SWAP_CREATED", requestingEmployeeId, shiftId, Map.of(
                "swapRequestId", swapRequestId,
                "requestingEmployeeId", requestingEmployeeId,
                "requestingEmployeeName", requestingEmployeeName,
                "targetEmployeeId", targetEmployeeId != null ? targetEmployeeId : -1L,
                "targetEmployeeName", targetEmployeeName != null ? targetEmployeeName : ""
        ));
    }

    public void logSwapApproved(Long swapRequestId, Long shiftId,
                                Long requestingEmployeeId, String requestingEmployeeName,
                                Long targetEmployeeId, String targetEmployeeName) {
        send("SWAP_APPROVED", requestingEmployeeId, shiftId, Map.of(
                "swapRequestId", swapRequestId,
                "requestingEmployeeId", requestingEmployeeId,
                "requestingEmployeeName", requestingEmployeeName,
                "targetEmployeeId", targetEmployeeId != null ? targetEmployeeId : -1L,
                "targetEmployeeName", targetEmployeeName != null ? targetEmployeeName : ""
        ));
    }

    public void logSwapRejected(Long swapRequestId, Long shiftId,
                                Long requestingEmployeeId, String requestingEmployeeName,
                                Long targetEmployeeId, String targetEmployeeName) {
        send("SWAP_REJECTED", requestingEmployeeId, shiftId, Map.of(
                "swapRequestId", swapRequestId,
                "requestingEmployeeId", requestingEmployeeId,
                "requestingEmployeeName", requestingEmployeeName,
                "targetEmployeeId", targetEmployeeId != null ? targetEmployeeId : -1L,
                "targetEmployeeName", targetEmployeeName != null ? targetEmployeeName : ""
        ));
    }

    public void logSwapResponse(Long swapRequestId, Long shiftId,
                                Long requestingEmployeeId, String requestingEmployeeName,
                                Long targetEmployeeId, String targetEmployeeName,
                                boolean willingToCover, String comment, Long locationId) {
        try {
            Map<String, Object> metadata = Map.of(
                    "swapRequestId", swapRequestId,
                    "requestingEmployeeId", requestingEmployeeId,
                    "requestingEmployeeName", requestingEmployeeName,
                    "targetEmployeeId", targetEmployeeId,
                    "targetEmployeeName", targetEmployeeName,
                    "willingToCover", willingToCover,
                    "comment", comment == null ? "" : comment,
                    "locationId", locationId
            );
            Map<String, Object> body = Map.of(
                    "eventType", "SWAP_RESPONSE",
                    "actorId", String.valueOf(targetEmployeeId),
                    "shiftId", String.valueOf(shiftId),
                    "metadata", metadata
            );
            restTemplate.postForObject("http://notification-service/events", body, Object.class);
        } catch (Exception e) {
            log.warn("Failed to notify notification-service of swap response for swapRequestId={}: {}",
                    swapRequestId, e.getMessage());
        }
    }

    private void send(String eventType, Long actorId, Long shiftId, Map<String, Object> metadata) {
        try {
            Map<String, Object> body = Map.of(
                    "eventType", eventType,
                    "actorId", String.valueOf(actorId),
                    "shiftId", String.valueOf(shiftId),
                    "metadata", metadata
            );
            restTemplate.postForObject("http://notification-service/events", body, Object.class);
        } catch (Exception e) {
            log.warn("Failed to notify notification-service of {} for shiftId={}: {}", eventType, shiftId, e.getMessage());
        }
    }
}
