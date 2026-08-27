package com.shiftsync.scheduling_service.config;

import com.shiftsync.scheduling_service.entities.Business;
import com.shiftsync.scheduling_service.entities.Employee;
import com.shiftsync.scheduling_service.entities.Location;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.repository.BusinessRepository;
import com.shiftsync.scheduling_service.repository.EmployeeRepository;
import com.shiftsync.scheduling_service.repository.LocationRepository;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final BusinessRepository businessRepository;
    private final LocationRepository locationRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    public DataInitializer(BusinessRepository businessRepository,
                           LocationRepository locationRepository,
                           EmployeeRepository employeeRepository,
                           ShiftRepository shiftRepository) {
        this.businessRepository = businessRepository;
        this.locationRepository = locationRepository;
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
    }

    @Override
    public void run(String... args) {
        if (locationRepository.count() == 0) {
            Business business = new Business();
            business.setName("ShiftSync Global Corp");
            business = businessRepository.save(business);

            Location loc1 = new Location();
            loc1.setName("Downtown HQ - Main Store");
            loc1.setAddress("100 Market St, San Francisco, CA");
            loc1.setBusiness(business);
            loc1 = locationRepository.save(loc1);

            Employee emp1 = new Employee();
            emp1.setName("Alex Mercer");
            emp1.setEmail("alex.mercer@shiftsync.io");
            emp1.setRole("Manager");
            emp1.setLocation(loc1);
            emp1 = employeeRepository.save(emp1);

            Employee emp2 = new Employee();
            emp2.setName("Sarah Connor");
            emp2.setEmail("sarah.c@shiftsync.io");
            emp2.setRole("Staff");
            emp2.setLocation(loc1);
            emp2 = employeeRepository.save(emp2);

            Employee emp3 = new Employee();
            emp3.setName("Marcus Vance");
            emp3.setEmail("marcus.v@shiftsync.io");
            emp3.setRole("Staff");
            emp3.setLocation(loc1);
            emp3 = employeeRepository.save(emp3);

            // Initial Shifts
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
            Shift shift1 = new Shift();
            shift1.setLocation(loc1);
            shift1.setEmployee(emp2);
            shift1.setStartTime(tomorrow);
            shift1.setEndTime(tomorrow.plusHours(8));
            shift1.setStatus(Shift.Status.SCHEDULED);
            shiftRepository.save(shift1);

            Shift shift2 = new Shift();
            shift2.setLocation(loc1);
            shift2.setEmployee(null);
            shift2.setStartTime(tomorrow.plusDays(1));
            shift2.setEndTime(tomorrow.plusDays(1).plusHours(8));
            shift2.setStatus(Shift.Status.OPEN);
            shiftRepository.save(shift2);

            System.out.println(">>> DataInitializer: Successfully seeded initial Business, Location, Employees and Shifts.");
        }
    }
}
