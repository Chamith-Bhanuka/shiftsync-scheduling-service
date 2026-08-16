package com.shiftsync.scheduling_service.service;

import com.shiftsync.scheduling_service.client.NotificationClient;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.entities.SwapRequest;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import com.shiftsync.scheduling_service.repository.SwapRequestRepository;
import org.springframework.stereotype.Service;

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

        notificationClient.logSwapApproved(saved.getId(), shift.getId(), request.getTargetEmployee().getId());

        return saved;
    }

    public SwapRequest reject(Long swapRequestId) {
        SwapRequest request = swapRequestRepository.findById(swapRequestId)
                .orElseThrow(() -> new RuntimeException("Swap request not found"));

        request.setStatus(SwapRequest.Status.REJECTED);
        SwapRequest saved = swapRequestRepository.save(request);

        notificationClient.logSwapRejected(saved.getId(), request.getShift().getId(), request.getRequestingEmployee().getId());

        return saved;
    }
}
