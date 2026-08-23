package com.shiftsync.scheduling_service.repository;

import com.shiftsync.scheduling_service.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByLocationId(Long locationId);
    List<Shift> findByLocationIdAndEmployeeIsNotNull(Long locationId);
}
