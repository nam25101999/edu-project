package com.edu.university.modules.student.service;

import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;
import com.edu.university.modules.student.dto.request.StudentBulkDeleteRequestDTO;
import com.edu.university.modules.student.dto.request.StudentBulkStatusRequestDTO;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentImportResultDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.dto.response.StudentStatsResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);

    Page<StudentResponseDTO> getAllStudents(String search, Boolean isActive, Pageable pageable);

    StudentResponseDTO getStudentById(UUID id);

    StudentResponseDTO getStudentByCode(String studentCode);

    StudentResponseDTO updateStudent(UUID id, StudentRequestDTO requestDTO);

    void deleteStudent(UUID id);

    void bulkDeleteStudents(StudentBulkDeleteRequestDTO requestDTO);

    StudentResponseDTO changeStatus(UUID id, StudentStatusChangeRequestDTO requestDTO);

    List<StudentResponseDTO> bulkChangeStatus(StudentBulkStatusRequestDTO requestDTO);

    StudentStatsResponseDTO getStudentStats();

    StudentImportResultDTO importStudents(MultipartFile file);

    byte[] exportStudents(String search, Boolean isActive, Pageable pageable);

    Page<StudentResponseDTO> getStudentsByStudentClass(UUID studentClassId, String search, Boolean isActive, Pageable pageable);

    Page<StudentResponseDTO> getStudentsByCourseSection(UUID courseSectionId, String search, Boolean isActive, Pageable pageable);

    StudentResponseDTO getCurrentStudentProfile();

    Page<StudentScheduleResponseDTO> getMySchedule(Pageable pageable);
}
