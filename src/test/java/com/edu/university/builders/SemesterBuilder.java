package com.edu.university.builders;

import com.edu.university.modules.academic.entity.Semester;
import java.time.LocalDate;
import java.util.UUID;

public class SemesterBuilder {
    private String semesterCode = "HK" + UUID.randomUUID().toString().substring(0, 3);
    private String semesterName = "Default Semester";
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now().plusMonths(4);

    public static SemesterBuilder aSemester() {
        return new SemesterBuilder();
    }

    public SemesterBuilder withSemesterCode(String semesterCode) {
        this.semesterCode = semesterCode;
        return this;
    }

    public SemesterBuilder withSemesterName(String semesterName) {
        this.semesterName = semesterName;
        return this;
    }

    public Semester build() {
        return Semester.builder()
                .semesterCode(semesterCode)
                .semesterName(semesterName)
                .startDate(startDate)
                .endDate(endDate)
                .isActive(true)
                .build();
    }
}
