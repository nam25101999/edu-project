package com.edu.university.builders;

import com.edu.university.modules.academic.entity.AcademicYear;
import java.util.UUID;

public class AcademicYearBuilder {
    private String academicCode = "K" + UUID.randomUUID().toString().substring(0, 2);
    private String academicYear = "2020-2024";

    public static AcademicYearBuilder anAcademicYear() {
        return new AcademicYearBuilder();
    }

    public AcademicYearBuilder withAcademicCode(String academicCode) {
        this.academicCode = academicCode;
        return this;
    }

    public AcademicYearBuilder withAcademicYear(String academicYear) {
        this.academicYear = academicYear;
        return this;
    }

    public AcademicYear build() {
        return AcademicYear.builder()
                .academicCode(academicCode)
                .academicYear(academicYear)
                .isActive(true)
                .build();
    }
}
