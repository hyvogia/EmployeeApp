package com.microservice.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.microservice.modal.Employee;
import com.microservice.repository.EmployeeRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository repo;

    public DataLoader(EmployeeRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() == 0) {
            List<Employee> list = List.of(
                make("Alice Admin","alice@example.com",LocalDate.of(2024,1,15),"Admin","Active"),
                make("Bob Publisher","bob@example.com",LocalDate.of(2024,2,10),"Publisher","Pending"),
                make("Carol Reviewer","carol@example.com",LocalDate.of(2024,3,5),"Reviewer","Active"),
                make("Dave Moderator","dave@example.com",LocalDate.of(2024,4,20),"Moderator","Suspended"),
                make("Eve Publisher","eve@example.com",LocalDate.of(2024,5,11),"Publisher","Active"),
                make("Frank Reviewer","frank@example.com",LocalDate.of(2024,6,30),"Reviewer","Inactive"),
                make("Grace Admin","grace@example.com",LocalDate.of(2024,7,22),"Admin","Active"),
                make("Heidi Moderator","heidi@example.com",LocalDate.of(2024,8,14),"Moderator","Pending"),
                make("Ivan Publisher","ivan@example.com",LocalDate.of(2024,9,1),"Publisher","Active"),
                make("Judy Reviewer","judy@example.com",LocalDate.of(2024,10,6),"Reviewer","Suspended"),
                make("Kate Admin","kate@example.com",LocalDate.of(2024,11,2),"Admin","Active"),
                make("Leo Publisher","leo@example.com",LocalDate.of(2024,11,12),"Publisher","Active"),
                make("Mallory Reviewer","mallory@example.com",LocalDate.of(2024,11,23),"Reviewer","Pending"),
                make("Nina Moderator","nina@example.com",LocalDate.of(2024,12,1),"Moderator","Active"),
                make("Oscar Publisher","oscar@example.com",LocalDate.of(2024,12,10),"Publisher","Inactive"),
                make("Peggy Reviewer","peggy@example.com",LocalDate.of(2024,12,15),"Reviewer","Active"),
                make("Quentin Admin","quentin@example.com",LocalDate.of(2024,12,20),"Admin","Suspended"),
                make("Rupert Moderator","rupert@example.com",LocalDate.of(2024,12,24),"Moderator","Active"),
                make("Sybil Publisher","sybil@example.com",LocalDate.of(2024,12,28),"Publisher","Pending"),
                make("Trent Reviewer","trent@example.com",LocalDate.of(2025,1,5),"Reviewer","Active")
            );
            repo.saveAll(list);
        }
    }

    private static Employee make(String name, String email, LocalDate date, String role, String status) {
        Employee e = new Employee();
        e.setName(name);
        e.setEmail(email);
        e.setCreatedDate(date);
        e.setRole(role);
        e.setStatus(status);
        return e;
    }
}
