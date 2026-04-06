package com.edu.university.modules.student.mapper;

import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import com.edu.university.modules.student.entity.StudentClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentClassMapper {

    // 1. Chiều Request DTO -> Entity (Create)
    // Bỏ qua academicYear để tránh lỗi ép kiểu (sẽ set thủ công ở Service)
    @Mapping(target = "academicYear", ignore = true)
    // Các trường departmentId, majorId ở DTO không tự động map vào object department, major được
    // Ta cũng sẽ bỏ qua và set chúng ở tầng Service
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "major", ignore = true)
    StudentClass toEntity(StudentClassRequestDTO requestDTO);

    // Chiều Entity -> Response DTO (Read)
    @Mapping(target = "academicYear", source = "academicYear.academicYear")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "majorId", source = "major.id")
    @Mapping(target = "majorName", source = "major.name")
    StudentClassResponseDTO toResponseDTO(StudentClass studentClass);

    // 3. Chiều Request DTO -> Entity (Update)
    // Bỏ qua các trường object phức tạp, tự xử lý ở Service
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "major", ignore = true)
    void updateEntityFromDTO(StudentClassRequestDTO requestDTO, @MappingTarget StudentClass studentClass);
}