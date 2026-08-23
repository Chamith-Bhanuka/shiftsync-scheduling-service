package com.shiftsync.scheduling_service.service;

import com.shiftsync.scheduling_service.client.NotificationClient;
import com.shiftsync.scheduling_service.dto.SwapResponseRequest;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.entities.SwapRequest;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import com.shiftsync.scheduling_service.repository.SwapRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SwapService {
    private final SwapRequestRepository swapRequestRepository;
    private final ShiftRepository shiftRepository;
    private final NotificationClient notificationClient;

    public SwapService(SwapRequestRepository swapRequestRepository,
                       ShiftRepository shiftRepository,
                       NotificationClient notificationClient) {
        this.swapRequestRepository = swapRequestRepository;
        this.shiftRepository = shiftRepository;
        this.notificationClient = notificationClient;
    }

    public SwapRequest approve(Long swapRequestId) {
        SwapRequest request = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));

        Shift shift = request.getShift();
        shift.setEmployee(request.getTargetEmployee());
        shift.setStatus(Shift.Status.COVERED);
        shiftRepository.save(shift);

        request.setStatus(SwapRequest.Status.APPROVED);
        SwapRequest saved = swapRequestRepository.save(request);

        notificationClient.logSwapApproved(
                saved.getId(), shift.getId(),
                request.getRequestingEmployee().getId(), request.getRequestingEmployee().getName(),
                request.getTargetEmployee().getId(), request.getTargetEmployee().getName()
        );

        return saved;
    }

    public SwapRequest reject(Long swapRequestId) {
        SwapRequest request = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));
        request.setStatus(SwapRequest.Status.REJECTED);
        SwapRequest saved = swapRequestRepository.save(request);

        notificationClient.logSwapRejected(
                saved.getId(), request.getShift().getId(),
                request.getRequestingEmployee().getId(), request.getRequestingEmployee().getName(),
                request.getTargetEmployee().getId(), request.getTargetEmployee().getName()
        );

        return saved;
    }

    public SwapRequest respond(Long swapRequestId, SwapResponseRequest response) {
        SwapRequest request = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));

        if (!request.getTargetEmployee().getId().equals(response.getEmployeeId())) {
            throw new RuntimeException("Only the target employee of this swap request can respond to it");
        }

        String prefix = response.isWillingToCover() ? "[WILLING] " : "[DECLINED] ";
        request.setEmployeeResponse(prefix + response.getComment());
        request.setRespondedAt(LocalDateTime.now());
        request.setStatus(SwapRequest.Status.RESPONDED);
        SwapRequest saved = swapRequestRepository.save(request);

        notificationClient.logSwapResponse(
                saved.getId(), saved.getShift().getId(),
                request.getRequestingEmployee().getId(), request.getRequestingEmployee().getName(),
                request.getTargetEmployee().getId(), request.getTargetEmployee().getName(),
                response.isWillingToCover(), response.getComment(),
                request.getShift().getLocation().getId()
        );

        return saved;
    }
}
