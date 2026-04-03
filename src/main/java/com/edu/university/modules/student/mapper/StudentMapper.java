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

    // 1. Chiều Create (Request DTO -> Entity)
    // Nối firstName và lastName từ DTO thành fullName cho Entity
    @Mapping(target = "fullName", expression = "java(requestDTO.getFirstName() + \" \" + requestDTO.getLastName())")
    // Bỏ qua các trường Object khóa ngoại để tránh lỗi, sẽ set bằng Service
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "trainingProgram", ignore = true)
    // Bỏ qua gender vì khác kiểu dữ liệu (DTO: Integer, Entity: String), sẽ convert thủ công ở Service
    @Mapping(target = "gender", ignore = true)
    Student toEntity(StudentRequestDTO requestDTO);

    // 2. Chiều Read (Entity -> Response DTO)
    // Lấy ID từ các đối tượng quan hệ
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "majorId", source = "major.id")
    @Mapping(target = "programId", source = "trainingProgram.id")
    // Nếu Entity Users có email và phone, bạn có thể mở comment 2 dòng dưới để tự động lấy:
    // @Mapping(target = "email", source = "user.email")
    // @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "firstName", ignore = true) // Entity ko có trường này
    @Mapping(target = "lastName", ignore = true)  // Entity ko có trường này
    @Mapping(target = "gender", ignore = true)    // Cần convert String -> Integer ở Service
    StudentResponseDTO toResponseDTO(Student student);

    // 3. Chiều Update (Request DTO -> Entity)
    @Mapping(target = "fullName", expression = "java(requestDTO.getFirstName() + \" \" + requestDTO.getLastName())")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "gender", ignore = true)
    void updateEntityFromDTO(StudentRequestDTO requestDTO, @MappingTarget Student student);
}