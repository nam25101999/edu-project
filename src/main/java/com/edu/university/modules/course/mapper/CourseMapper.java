package com.edu.university.modules.course.mapper;

// Sửa lại import: Trỏ thẳng đến CourseRequest bên trong CourseDtos
import com.edu.university.modules.course.dto.CourseDtos.CourseRequest;
import com.edu.university.modules.course.entity.Course; // Lưu ý: Trỏ đúng package chứa Entity Course của bạn
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    Course toEntity(CourseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    void updateEntityFromDto(CourseRequest request, @MappingTarget Course entity);
}