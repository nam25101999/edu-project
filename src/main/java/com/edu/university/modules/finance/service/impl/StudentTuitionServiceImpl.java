package com.edu.university.modules.finance.service.impl;

import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;
import com.edu.university.modules.finance.entity.StudentTuition;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.mapper.StudentTuitionMapper;
import com.edu.university.modules.finance.repository.StudentTuitionRepository;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.finance.service.StudentTuitionService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentTuitionServiceImpl implements StudentTuitionService {

    private final StudentTuitionRepository studentTuitionRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final StudentTuitionMapper studentTuitionMapper;

    @Override
    @Transactional
    public StudentTuitionResponseDTO create(StudentTuitionRequestDTO requestDTO) {
        StudentTuition tuition = studentTuitionMapper.toEntity(requestDTO);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ"));
        
        if (requestDTO.getTuitionFeeId() != null) {
            TuitionFee fee = tuitionFeeRepository.findById(requestDTO.getTuitionFeeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy định mức học phí"));
            tuition.setTuitionFee(fee);
        }
        
        tuition.setStudent(student);
        tuition.setSemester(semester);
        tuition.setActive(true);
        tuition.setCreatedAt(LocalDateTime.now());
        
        return studentTuitionMapper.toResponseDTO(studentTuitionRepository.save(tuition));
    }

    @Override
    public List<StudentTuitionResponseDTO> getByStudentId(UUID studentId) {
        return studentTuitionRepository.findByStudentId(studentId).stream()
                .map(studentTuitionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentTuitionResponseDTO> getBySemesterId(UUID semesterId) {
        return studentTuitionRepository.findBySemesterId(semesterId).stream()
                .map(studentTuitionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentTuitionResponseDTO update(UUID id, StudentTuitionRequestDTO requestDTO) {
        StudentTuition tuition = studentTuitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học phí sinh viên"));
        studentTuitionMapper.updateEntityFromDTO(requestDTO, tuition);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ"));
        
        if (requestDTO.getTuitionFeeId() != null) {
            TuitionFee fee = tuitionFeeRepository.findById(requestDTO.getTuitionFeeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy định mức học phí"));
            tuition.setTuitionFee(fee);
        }
        
        tuition.setStudent(student);
        tuition.setSemester(semester);
        tuition.setUpdatedAt(LocalDateTime.now());
        
        return studentTuitionMapper.toResponseDTO(studentTuitionRepository.save(tuition));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentTuition tuition = studentTuitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học phí sinh viên"));
        tuition.softDelete("system");
        studentTuitionRepository.save(tuition);
    }
}
