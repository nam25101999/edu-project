package com.edu.university.builders;

import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.hr.entity.Department;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TrainingProgramBuilder {
    private String programCode = "TP_" + UUID.randomUUID().toString().substring(0, 5);
    private String programName = "Default Program";
    private Major major;
    private Department department;
    private String degreeLevel = "BACHELOR";
    private String educationType = "FULL_TIME";
    private BigDecimal totalCredits = new BigDecimal("120.0");
    private String version = "1.0";
    private String status = "ACTIVE";

    public static TrainingProgramBuilder aTrainingProgram() {
        return new TrainingProgramBuilder();
    }

    public TrainingProgramBuilder withProgramCode(String programCode) {
        this.programCode = programCode;
        return this;
    }

    public TrainingProgramBuilder withProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public TrainingProgramBuilder withMajor(Major major) {
        this.major = major;
        return this;
    }

    public TrainingProgramBuilder withDepartment(Department department) {
        this.department = department;
        return this;
    }

    public TrainingProgram build() {
        return TrainingProgram.builder()
                .programCode(programCode)
                .programName(programName)
                .major(major)
                .department(department)
                .degreeLevel(degreeLevel)
                .educationType(educationType)
                .totalCredits(totalCredits)
                .version(version)
                .status(status)
                .isActive(true)
                .build();
    }
}
