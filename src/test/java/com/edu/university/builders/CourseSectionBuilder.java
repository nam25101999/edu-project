package com.edu.university.builders;

import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.curriculum.entity.Course;
import java.util.UUID;

public class CourseSectionBuilder {
    private String classCode = "CLASS_" + UUID.randomUUID().toString().substring(0, 5);
    private Course course;
    private Semester semester;
    private Integer maxStudents = 50;
    private Integer minStudents = 10;
    private String classType = "THEORY";

    public static CourseSectionBuilder aCourseSection() {
        return new CourseSectionBuilder();
    }

    public CourseSectionBuilder withClassCode(String classCode) {
        this.classCode = classCode;
        return this;
    }

    public CourseSectionBuilder withCourse(Course course) {
        this.course = course;
        return this;
    }

    public CourseSectionBuilder withSemester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public CourseSection build() {
        return CourseSection.builder()
                .classCode(classCode)
                .course(course)
                .semester(semester)
                .maxStudents(maxStudents)
                .minStudents(minStudents)
                .classType(classType)
                .isActive(true)
                .build();
    }
}
