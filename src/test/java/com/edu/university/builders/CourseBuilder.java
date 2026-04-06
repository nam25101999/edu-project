package com.edu.university.builders;

import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.hr.entity.Department;
import java.math.BigDecimal;
import java.util.UUID;

public class CourseBuilder {
    private String code = "CRS_" + UUID.randomUUID().toString().substring(0, 5);
    private String name = "Default Course";
    private String courseNameEn = "Default Course EN";
    private BigDecimal credits = new BigDecimal("3.0");
    private String courseType = "REQUIRED";
    private BigDecimal theoryHours = new BigDecimal("30.0");
    private BigDecimal practiceHours = new BigDecimal("15.0");
    private BigDecimal selfStudyHours = new BigDecimal("45.0");
    private Department department;

    public static CourseBuilder aCourse() {
        return new CourseBuilder();
    }

    public CourseBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public CourseBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CourseBuilder withDepartment(Department department) {
        this.department = department;
        return this;
    }

    public Course build() {
        return Course.builder()
                .code(code)
                .name(name)
                .courseNameEn(courseNameEn)
                .credits(credits)
                .courseType(courseType)
                .theoryHours(theoryHours)
                .practiceHours(practiceHours)
                .selfStudyHours(selfStudyHours)
                .department(department)
                .isActive(true)
                .build();
    }
}
