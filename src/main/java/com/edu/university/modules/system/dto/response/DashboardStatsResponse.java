package com.edu.university.modules.system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalStudents;
    private long totalLecturers;
    private long totalCourses;
    private long totalMajors;
}
