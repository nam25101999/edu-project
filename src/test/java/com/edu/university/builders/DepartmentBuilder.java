package com.edu.university.builders;

import com.edu.university.modules.hr.entity.Department;
import java.time.LocalDate;
import java.util.UUID;

public class DepartmentBuilder {
    private String code = "DEPT_" + UUID.randomUUID().toString().substring(0, 5);
    private String name = "Default Department";
    private String description = "Default Description";
    private LocalDate establishedDate = LocalDate.now();
    private boolean isActive = true;

    public static DepartmentBuilder aDepartment() {
        return new DepartmentBuilder();
    }

    public DepartmentBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public DepartmentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Department build() {
        return Department.builder()
                .code(code)
                .name(name)
                .description(description)
                .establishedDate(establishedDate)
                .isActive(isActive)
                .build();
    }
}
