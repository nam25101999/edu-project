package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);
    List<StudentResponseDTO> getAllStudents();
    StudentResponseDTO getStudentById(UUID id);
    StudentResponseDTO getStudentByCode(String studentCode);
    StudentResponseDTO updateStudent(UUID id, StudentRequestDTO requestDTO);
    void deleteStudent(UUID id);
    StudentResponseDTO changeStatus(UUID id, StudentStatusChangeRequestDTO requestDTO);
    
    // Lấy thông tin sinh viên đang đăng nhập dựa trên token
    StudentResponseDTO getCurrentStudentProfile();

    // Lấy lịch học của sinh viên đang đăng nhập
    List<StudentScheduleResponseDTO> getMySchedule();
}