package com.edu.university.builders;

import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.student.entity.StudentClass;
import java.util.UUID;

public class StudentClassBuilder {
    private String classCode = "CLASS_" + UUID.randomUUID().toString().substring(0, 5);
    private String className = "Default Class";
    private Department department;
    private Major major;
    private AcademicYear academicYear;

    public static StudentClassBuilder aStudentClass() {
        return new StudentClassBuilder();
    }

    public StudentClassBuilder withClassCode(String classCode) {
        this.classCode = classCode;
        return this;
    }

    public StudentClassBuilder withClassName(String className) {
        this.className = className;
        return this;
    }

    public StudentClassBuilder withDepartment(Department department) {
        this.department = department;
        return this;
    }

    public StudentClassBuilder withMajor(Major major) {
        this.major = major;
        return this;
    }

    public StudentClassBuilder withAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
        return this;
    }

    public StudentClass build() {
        return StudentClass.builder()
                .classCode(classCode)
                .className(className)
                .department(department)
                .major(major)
                .academicYear(academicYear)
                .isActive(true)
                .build();
    }
}
