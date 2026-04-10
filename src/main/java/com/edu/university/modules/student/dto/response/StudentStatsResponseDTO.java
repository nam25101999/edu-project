package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentStatsResponseDTO {
    private long totalStudents;
    private long activeStudents;
    private long inactiveStudents;
}
