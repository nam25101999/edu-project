package com.edu.university.modules.student.mapper;

import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "fullName", expression = "java(buildFullName(requestDTO.getFirstName(), requestDTO.getLastName()))")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    @Mapping(target = "gender", expression = "java(mapGenderToEntity(requestDTO.getGender()))")
    Student toEntity(StudentRequestDTO requestDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "majorId", source = "major.id")
    @Mapping(target = "majorName", source = "major.name")
    @Mapping(target = "programId", source = "trainingProgram.id")
    @Mapping(target = "programName", source = "trainingProgram.programName")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "gender", expression = "java(mapGenderToDto(student.getGender()))")
    StudentResponseDTO toResponseDTO(Student student);

    @Mapping(target = "fullName", expression = "java(buildFullName(requestDTO.getFirstName(), requestDTO.getLastName()))")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    @Mapping(target = "gender", expression = "java(mapGenderToEntity(requestDTO.getGender()))")
    void updateEntityFromDTO(StudentRequestDTO requestDTO, @MappingTarget Student student);

    default String buildFullName(String firstName, String lastName) {
        String safeFirstName = firstName == null ? "" : firstName.trim();
        String safeLastName = lastName == null ? "" : lastName.trim();
        return (safeFirstName + " " + safeLastName).trim();
    }

    default String mapGenderToEntity(Integer gender) {
        return gender == null ? null : String.valueOf(gender);
    }

    default Integer mapGenderToDto(String gender) {
        if (gender == null || gender.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(gender);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
