package com.shiftsync.scheduling_service.repository;

import com.shiftsync.scheduling_service.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByBusinessId(Long businessId);
}
