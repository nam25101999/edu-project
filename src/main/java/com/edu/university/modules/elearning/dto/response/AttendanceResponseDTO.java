package com.edu.university.modules.elearning.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponseDTO {
    private UUID id;
    private UUID courseSectionId;
    private String classCode;
    private UUID scheduleId;
    private LocalDate attendanceDate;
    private String notes;
    private List<AttendanceRecordResponseDTO> records;
}
