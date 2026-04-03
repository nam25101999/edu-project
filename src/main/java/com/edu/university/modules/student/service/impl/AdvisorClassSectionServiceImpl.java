package com.edu.university.modules.student.service.impl;

import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;
import com.edu.university.modules.student.entity.AdvisorClassSection;
import com.edu.university.modules.student.mapper.AdvisorClassSectionMapper;
import com.edu.university.modules.student.repository.AdvisorClassSectionRepository;
import com.edu.university.modules.student.service.AdvisorClassSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisorClassSectionServiceImpl implements AdvisorClassSectionService {

    private final AdvisorClassSectionRepository repository;
    private final AdvisorClassSectionMapper mapper;

    @Override
    @Transactional
    public AdvisorClassSectionResponseDTO assignAdvisorToClass(AdvisorClassSectionRequestDTO requestDTO) {
        // Cập nhật tên hàm gọi repository
        if(repository.existsByAdvisorIdAndStudentClassId(requestDTO.getAdvisorId(), requestDTO.getStudentClassesId())){
            throw new RuntimeException("Cố vấn này đã được phân công cho lớp này");
        }
        AdvisorClassSection entity = mapper.toEntity(requestDTO);
        entity.setActive(true);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    public List<AdvisorClassSectionResponseDTO> getAll() {
        return repository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AdvisorClassSectionResponseDTO> getByAdvisorId(UUID advisorId) {
        return repository.findByAdvisorId(advisorId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<AdvisorClassSectionResponseDTO> getByClassId(UUID studentClassesId) {
        // Cập nhật tên hàm gọi repository
        return repository.findByStudentClassId(studentClassesId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdvisorClassSectionResponseDTO update(UUID id, AdvisorClassSectionRequestDTO requestDTO) {
        AdvisorClassSection entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        mapper.updateEntityFromDTO(requestDTO, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AdvisorClassSection entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}