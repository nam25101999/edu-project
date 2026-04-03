package com.edu.university.modules.student.mapper;

import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;
import com.edu.university.modules.student.entity.StudentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentStatusMapper {

    // Bỏ qua object student khi map từ DTO -> Entity (Sẽ set ở tầng Service)
    @Mapping(target = "student", ignore = true)
    StudentStatus toEntity(StudentStatusRequestDTO requestDTO);

    // Lấy thông tin từ object student bên trong Entity gán ra DTO
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "studentName", source = "student.fullName")
    StudentStatusResponseDTO toResponseDTO(StudentStatus entity);

    @Mapping(target = "student", ignore = true)
    void updateEntityFromDTO(StudentStatusRequestDTO requestDTO, @MappingTarget StudentStatus entity);
}