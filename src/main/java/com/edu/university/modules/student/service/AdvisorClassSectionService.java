package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdvisorClassSectionService {
    AdvisorClassSectionResponseDTO assignAdvisorToClass(AdvisorClassSectionRequestDTO requestDTO);
    Page<AdvisorClassSectionResponseDTO> getAll(Pageable pageable);
    Page<AdvisorClassSectionResponseDTO> getByAdvisorId(UUID advisorId, Pageable pageable);
    Page<AdvisorClassSectionResponseDTO> getByClassId(UUID studentClassesId, Pageable pageable);
    AdvisorClassSectionResponseDTO update(UUID id, AdvisorClassSectionRequestDTO requestDTO);
    void delete(UUID id);
}