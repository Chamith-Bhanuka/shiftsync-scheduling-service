package com.shiftsync.scheduling_service.controller;

import com.shiftsync.scheduling_service.dto.BusinessRequest;
import com.shiftsync.scheduling_service.entities.Business;
import com.shiftsync.scheduling_service.repository.BusinessRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses")
public class BusinessController {
    private final BusinessRepository businessRepository;

    public BusinessController(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @PostMapping
    public Business create(@RequestBody BusinessRequest request) {
        Business business = new Business();
        business.setName(request.getName());
        return businessRepository.save(business);
    }

    @GetMapping
    public List<Business> getAll() {
        return businessRepository.findAll();
    }
}
