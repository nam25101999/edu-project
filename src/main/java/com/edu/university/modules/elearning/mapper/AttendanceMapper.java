package com.edu.university.modules.elearning.mapper;

import com.edu.university.modules.elearning.dto.response.AttendanceRecordResponseDTO;
import com.edu.university.modules.elearning.dto.response.AttendanceResponseDTO;
import com.edu.university.modules.elearning.entity.Attendance;
import com.edu.university.modules.elearning.entity.AttendanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "courseSection.id", target = "courseSectionId")
    @Mapping(source = "courseSection.classCode", target = "classCode")
    @Mapping(source = "schedule.id", target = "scheduleId")
    AttendanceResponseDTO toResponseDTO(Attendance attendance);

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "student.studentCode", target = "studentCode")
    AttendanceRecordResponseDTO toRecordResponseDTO(AttendanceRecord record);
}
