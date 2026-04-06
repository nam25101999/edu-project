package com.edu.university.modules.academic.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentCourseSectionRequestDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    @NotNull(message = "ID lớp học phần không được để trống")
    private UUID courseSectionId;

    private String status;
    private LocalDateTime registeredAt;
    private String note;
}
