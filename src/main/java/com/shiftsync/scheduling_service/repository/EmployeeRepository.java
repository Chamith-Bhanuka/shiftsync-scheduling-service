package com.shiftsync.scheduling_service.repository;

import com.shiftsync.scheduling_service.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByLocationId(Long locationId);
}
