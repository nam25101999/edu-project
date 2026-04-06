package com.edu.university.builders;

import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.auth.entity.Users;
import java.time.LocalDate;
import java.util.UUID;

public class StudentBuilder {
    private String studentCode = "B20DCCN" + UUID.randomUUID().toString().substring(0, 3);
    private String fullName = "Nguyen Van A";
    private Users user;
    private AcademicYear academicYear;
    private Department department;
    private Major major;
    private TrainingProgram trainingProgram;
    private StudentClass studentClass;

    public static StudentBuilder aStudent() {
        return new StudentBuilder();
    }

    public StudentBuilder withStudentCode(String studentCode) {
        this.studentCode = studentCode;
        return this;
    }

    public StudentBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public StudentBuilder withUser(Users user) {
        this.user = user;
        return this;
    }

    public StudentBuilder withStudentClass(StudentClass studentClass) {
        this.studentClass = studentClass;
        return this;
    }

    public Student build() {
        return Student.builder()
                .studentCode(studentCode)
                .fullName(fullName)
                .user(user)
                .academicYear(academicYear)
                .department(department)
                .major(major)
                .trainingProgram(trainingProgram)
                .studentClass(studentClass)
                .isActive(true)
                .build();
    }
}
