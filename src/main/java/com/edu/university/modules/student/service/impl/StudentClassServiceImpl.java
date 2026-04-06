package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.mapper.StudentClassMapper;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.service.StudentClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentClassServiceImpl implements StudentClassService {

    private final StudentClassRepository studentClassRepository;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentClassMapper studentClassMapper;

    @Override
    @Transactional
    public StudentClassResponseDTO createClass(StudentClassRequestDTO requestDTO) {
        if(studentClassRepository.existsByClassCode(requestDTO.getClassCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã lớp đã tồn tại");
        }
        
        StudentClass studentClass = studentClassMapper.toEntity(requestDTO);
        
        Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
        Major major = majorRepository.findById(requestDTO.getMajorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành"));
        AcademicYear academicYear = academicYearRepository.findByAcademicYear(requestDTO.getAcademicYear())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy niên khóa"));
                
        studentClass.setDepartment(department);
        studentClass.setMajor(major);
        studentClass.setAcademicYear(academicYear);
        studentClass.setActive(true);
        
        return studentClassMapper.toResponseDTO(studentClassRepository.save(studentClass));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentClassResponseDTO> getAllClasses(Pageable pageable) {
        return studentClassRepository.findAll(pageable)
                .map(studentClassMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentClassResponseDTO getClassById(UUID id) {
        return studentClassRepository.findById(id)
                .map(studentClassMapper::toResponseDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
    }

    @Override
    @Transactional
    public StudentClassResponseDTO updateClass(UUID id, StudentClassRequestDTO requestDTO) {
        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
        
        studentClassMapper.updateEntityFromDTO(requestDTO, studentClass);
        
        Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
        Major major = majorRepository.findById(requestDTO.getMajorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành"));
        AcademicYear academicYear = academicYearRepository.findByAcademicYear(requestDTO.getAcademicYear())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy niên khóa"));
                
        studentClass.setDepartment(department);
        studentClass.setMajor(major);
        studentClass.setAcademicYear(academicYear);
        
        return studentClassMapper.toResponseDTO(studentClassRepository.save(studentClass));
    }

    @Override
    @Transactional
    public void deleteClass(UUID id) {
        StudentClass studentClass = studentClassRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
        studentClass.softDelete("system");
        studentClassRepository.save(studentClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentClassResponseDTO> getClassesByDepartmentAndMajor(UUID departmentId, UUID majorId, Pageable pageable) {
        Page<StudentClass> classes;
        if (departmentId != null && majorId != null) {
            classes = studentClassRepository.findByDepartmentIdAndMajorId(departmentId, majorId, pageable);
        } else if (departmentId != null) {
            classes = studentClassRepository.findByDepartmentId(departmentId, pageable);
        } else if (majorId != null) {
            classes = studentClassRepository.findByMajorId(majorId, pageable);
        } else {
            classes = studentClassRepository.findAll(pageable);
        }
        return classes.map(studentClassMapper::toResponseDTO);
    }
}