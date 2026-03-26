package com.edu.university.modules.enrollment.dto;

public record GradeFullDto(
        String semester,
        Integer year,
        String courseCode,
        String courseName,
        Integer credits,
        Double attendance,
        Double midterm,
        Double finalScore,
        Double totalScore,
        String letterGrade,
        Double gpaScore
) {}