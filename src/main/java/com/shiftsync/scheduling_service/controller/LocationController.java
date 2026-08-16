package com.shiftsync.scheduling_service.controller;

import com.shiftsync.scheduling_service.dto.LocationRequest;
import com.shiftsync.scheduling_service.entities.Business;
import com.shiftsync.scheduling_service.entities.Location;
import com.shiftsync.scheduling_service.repository.BusinessRepository;
import com.shiftsync.scheduling_service.repository.LocationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationRepository locationRepository;
    private final BusinessRepository businessRepository;

    public LocationController(LocationRepository locationRepository, BusinessRepository businessRepository) {
        this.locationRepository = locationRepository;
        this.businessRepository = businessRepository;
    }

    @PostMapping
    public Location create(@RequestBody LocationRequest request) {
        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business not found: " + request.getBusinessId()));
        Location location = new Location();
        location.setBusiness(business);
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        return locationRepository.save(location);
    }

    @GetMapping
    public List<Location> getByBusiness(@RequestParam Long businessId) {
        return locationRepository.findByBusinessId(businessId);
    }
}
