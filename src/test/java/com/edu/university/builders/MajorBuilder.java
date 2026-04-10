package com.edu.university.builders;

import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.hr.entity.Faculty;

public class MajorBuilder {
    private String majorCode = "MAJ_TEST";
    private String name = "Test Major";
    private String description = "Test Description";
    private Faculty faculty;

    public static MajorBuilder aMajor() {
        return new MajorBuilder();
    }

    public MajorBuilder withCode(String majorCode) {
        this.majorCode = majorCode;
        return this;
    }

    public MajorBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MajorBuilder withFaculty(Faculty faculty) {
        this.faculty = faculty;
        return this;
    }

    public Major build() {
        return Major.builder()
                .majorCode(majorCode)
                .name(name)
                .description(description)
                .faculty(faculty)
                .isActive(true)
                .build();
    }
}
