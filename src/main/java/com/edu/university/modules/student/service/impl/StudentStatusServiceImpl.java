package com.edu.university.modules.student.service.impl;

import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;
import com.edu.university.modules.student.entity.StudentStatus;
import com.edu.university.modules.student.mapper.StudentStatusMapper;
import com.edu.university.modules.student.repository.StudentStatusRepository;
import com.edu.university.modules.student.service.StudentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentStatusServiceImpl implements StudentStatusService {

    private final StudentStatusRepository repository;
    private final StudentStatusMapper mapper;

    @Override
    @Transactional
    public StudentStatusResponseDTO createStatus(StudentStatusRequestDTO requestDTO) {
        StudentStatus entity = mapper.toEntity(requestDTO);
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    public List<StudentStatusResponseDTO> getAll() {
        return repository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<StudentStatusResponseDTO> getByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentStatusResponseDTO updateStatus(UUID id, StudentStatusRequestDTO requestDTO) {
        StudentStatus entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái"));
        mapper.updateEntityFromDTO(requestDTO, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteStatus(UUID id) {
        StudentStatus entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}