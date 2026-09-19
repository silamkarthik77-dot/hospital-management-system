package com.hms.config;

import com.hms.entity.Department;
import com.hms.entity.Role;
import com.hms.entity.RoleName;
import com.hms.repository.DepartmentRepository;
import com.hms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ensures the fixed role set and a starter list of departments exist on
 * every boot. Idempotent - safe to run against an already-seeded database
 * (checks existsBy before inserting), so it can stay enabled in all envs.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        }

        if (departmentRepository.count() == 0) {
            List<String> defaults = List.of("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "General Medicine");
            for (String name : defaults) {
                departmentRepository.save(Department.builder().name(name).description(name + " department").build());
            }
        }
    }
}
