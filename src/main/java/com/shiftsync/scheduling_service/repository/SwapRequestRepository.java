package com.shiftsync.scheduling_service.repository;

import com.shiftsync.scheduling_service.entities.SwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {
}
