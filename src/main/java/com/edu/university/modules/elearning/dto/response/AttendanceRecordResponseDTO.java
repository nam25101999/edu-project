package com.edu.university.modules.elearning.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private String status;
    private String note;
}
