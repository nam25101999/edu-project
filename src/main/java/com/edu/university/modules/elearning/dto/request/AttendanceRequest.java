package com.edu.university.modules.elearning.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {
    private UUID courseSectionId;
    private UUID scheduleId;
    private LocalDate attendanceDate;
    private String notes;
    private List<AttendanceRecordRequest> records;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceRecordRequest {
        private UUID studentId;
        private String status;
        private String note;
    }
}
