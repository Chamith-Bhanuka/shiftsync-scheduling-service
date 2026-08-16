package com.shiftsync.scheduling_service.repository;

import com.shiftsync.scheduling_service.entities.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {
}
