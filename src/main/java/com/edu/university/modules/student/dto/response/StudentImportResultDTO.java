package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentImportResultDTO {
    private int totalRows;
    private int createdCount;
    private int failedCount;
    private List<String> errors;
}
