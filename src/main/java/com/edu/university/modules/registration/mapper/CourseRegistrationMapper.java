package com.edu.university.modules.registration.mapper;

import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;
import com.edu.university.modules.registration.entity.CourseRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseRegistrationMapper {
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "courseSection", ignore = true)
    @Mapping(target = "registrationPeriod", ignore = true)
    CourseRegistration toEntity(CourseRegistrationRequestDTO requestDTO);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "courseSectionId", source = "courseSection.id")
    @Mapping(target = "classCode", source = "courseSection.classCode")
    @Mapping(target = "courseCode", source = "courseSection.course.code")
    @Mapping(target = "courseName", source = "courseSection.course.name")
    @Mapping(target = "registrationPeriodId", source = "registrationPeriod.id")
    @Mapping(target = "registrationPeriodName", source = "registrationPeriod.name")
    CourseRegistrationResponseDTO toResponseDTO(CourseRegistration courseRegistration);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "courseSection", ignore = true)
    @Mapping(target = "registrationPeriod", ignore = true)
    void updateEntityFromDTO(CourseRegistrationRequestDTO requestDTO,
            @MappingTarget CourseRegistration courseRegistration);
}
