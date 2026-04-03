package com.edu.university.modules.student.service.impl;

import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.mapper.StudentClassMapper;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.service.StudentClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassRepository studentClassRepository;
    private final StudentClassMapper studentClassMapper;

    @Override
    @Transactional
    public StudentClassResponseDTO createClass(StudentClassRequestDTO requestDTO) {
        if(studentClassRepository.existsByClassCode(requestDTO.getClassCode())) {
            throw new RuntimeException("Mã lớp đã tồn tại");
        }
        StudentClass studentClass = studentClassMapper.toEntity(requestDTO);
        studentClass.setActive(true);
        studentClass.setCreatedAt(LocalDateTime.now());
        return studentClassMapper.toResponseDTO(studentClassRepository.save(studentClass));
    }

    @Override
    public List<StudentClassResponseDTO> getAllClasses() {
        return studentClassRepository.findAll().stream()
                .map(studentClassMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentClassResponseDTO getClassById(UUID id) {
        return studentClassRepository.findById(id)
                .map(studentClassMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học"));
    }

    @Override
    @Transactional
    public StudentClassResponseDTO updateClass(UUID id, StudentClassRequestDTO requestDTO) {
        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học"));
        studentClassMapper.updateEntityFromDTO(requestDTO, studentClass);
        return studentClassMapper.toResponseDTO(studentClassRepository.save(studentClass));
    }

    @Override
    @Transactional
    public void deleteClass(UUID id) {
        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học"));
        studentClass.setActive(false);
        studentClass.setDeletedAt(LocalDateTime.now());
        studentClassRepository.save(studentClass);
    }

    @Override
    public List<StudentClassResponseDTO> getClassesByDepartmentAndMajor(UUID departmentId, UUID majorId) {
        List<StudentClass> classes;
        if (departmentId != null && majorId != null) {
            classes = studentClassRepository.findByDepartmentIdAndMajorId(departmentId, majorId);
        } else if (departmentId != null) {
            classes = studentClassRepository.findByDepartmentId(departmentId);
        } else if (majorId != null) {
            classes = studentClassRepository.findByMajorId(majorId);
        } else {
            classes = studentClassRepository.findAll();
        }
        return classes.stream().map(studentClassMapper::toResponseDTO).collect(Collectors.toList());
    }
}