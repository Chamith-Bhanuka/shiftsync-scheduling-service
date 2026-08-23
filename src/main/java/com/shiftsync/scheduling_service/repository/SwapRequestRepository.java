package com.shiftsync.scheduling_service.repository;

import com.shiftsync.scheduling_service.entities.SwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {
    List<SwapRequest> findByStatus(SwapRequest.Status status);
    List<SwapRequest> findByRequestingEmployeeIdOrTargetEmployeeId(Long requestingId, Long targetId);
}
