package com.edu.university.modules.student.mapper;

import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;
import com.edu.university.modules.student.entity.StudentClassSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentClassSectionMapper {

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    StudentClassSection toEntity(StudentClassSectionRequestDTO requestDTO);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentClassesId", source = "studentClass.id")
    @Mapping(target = "className", source = "studentClass.className")
    StudentClassSectionResponseDTO toResponseDTO(StudentClassSection entity);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    void updateEntityFromDTO(StudentClassSectionRequestDTO requestDTO, @MappingTarget StudentClassSection entity);
}