package com.edu.university.modules.curriculum.service;

import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.dto.response.MajorResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MajorService {
    MajorResponseDTO create(MajorRequestDTO requestDTO);
    List<MajorResponseDTO> getAll();
    MajorResponseDTO getById(UUID id);
    MajorResponseDTO update(UUID id, MajorRequestDTO requestDTO);
    void delete(UUID id);
}
