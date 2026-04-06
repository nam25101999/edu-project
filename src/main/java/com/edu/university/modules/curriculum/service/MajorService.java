package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.dto.response.MajorResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MajorService {
    MajorResponseDTO create(MajorRequestDTO requestDTO);
    Page<MajorResponseDTO> getAll(Pageable pageable);
    MajorResponseDTO getById(UUID id);
    MajorResponseDTO update(UUID id, MajorRequestDTO requestDTO);
    void delete(UUID id);
}
