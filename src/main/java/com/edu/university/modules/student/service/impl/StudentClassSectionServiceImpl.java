package com.edu.university.modules.student.service.impl;

import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;
import com.edu.university.modules.student.entity.StudentClassSection;
import com.edu.university.modules.student.mapper.StudentClassSectionMapper;
import com.edu.university.modules.student.repository.StudentClassSectionRepository;
import com.edu.university.modules.student.service.StudentClassSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentClassSectionServiceImpl implements StudentClassSectionService {

    private final StudentClassSectionRepository repository;
    private final StudentClassSectionMapper mapper;

    @Override
    @Transactional
    public StudentClassSectionResponseDTO addStudentToClass(StudentClassSectionRequestDTO requestDTO) {
        // Cập nhật tên hàm gọi repository
        if(repository.existsByStudentIdAndStudentClassId(requestDTO.getStudentId(), requestDTO.getStudentClassesId())){
            throw new RuntimeException("Sinh viên đã có trong lớp này");
        }
        StudentClassSection entity = mapper.toEntity(requestDTO);
        entity.setActive(true);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    public List<StudentClassSectionResponseDTO> getAll() {
        return repository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<StudentClassSectionResponseDTO> getByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<StudentClassSectionResponseDTO> getByClassId(UUID studentClassesId) {
        // Cập nhật tên hàm gọi repository
        return repository.findByStudentClassId(studentClassesId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentClassSectionResponseDTO update(UUID id, StudentClassSectionRequestDTO requestDTO) {
        StudentClassSection entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        mapper.updateEntityFromDTO(requestDTO, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentClassSection entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}