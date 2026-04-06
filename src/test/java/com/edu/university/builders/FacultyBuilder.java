package com.edu.university.builders;

import com.edu.university.modules.hr.entity.Faculty;
import java.time.LocalDate;

public class FacultyBuilder {
    private String code = "FAC_TEST";
    private String name = "Test Faculty";
    private String description = "Test Description";
    private LocalDate establishedYear = LocalDate.now();

    public static FacultyBuilder aFaculty() {
        return new FacultyBuilder();
    }

    public FacultyBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public FacultyBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Faculty build() {
        return Faculty.builder()
                .code(code)
                .name(name)
                .description(description)
                .establishedYear(establishedYear)
                .isActive(true)
                .build();
    }
}
