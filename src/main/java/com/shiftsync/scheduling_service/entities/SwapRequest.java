package com.shiftsync.scheduling_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "swap_requests")
@Setter
@Getter
public class SwapRequest {
    public enum Status { PENDING, RESPONDED, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "requesting_employee_id")
    private Employee requestingEmployee;

    @ManyToOne
    @JoinColumn(name = "target_employee_id")
    private Employee targetEmployee; // nullable

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private String employeeResponse; // free-text comment from the target employee, nullable

    private LocalDateTime respondedAt;

}
