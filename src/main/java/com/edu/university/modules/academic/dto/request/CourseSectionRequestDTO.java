package com.edu.university.modules.academic.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseSectionRequestDTO {
    @NotBlank(message = "Mã lớp học phần không được để trống")
    private String classCode;

    @NotNull(message = "ID môn học không được để trống")
    private UUID courseId;

    @NotNull(message = "ID học kỳ không được để trống")
    private UUID semesterId;

    private UUID majorId;

    @NotBlank(message = "Năm học không được để trống")
    private String academicYear;

    private UUID lecturerId;
    private UUID roomId;
    private UUID buildingId;
    private Integer maxStudents;
    private Integer minStudents;
    private String classType;
    private String status;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private String note;
    private Boolean isSystem;
}
