package com.shiftsync.scheduling_service.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private Long businessId;
    private String name;
    private String address;
}
