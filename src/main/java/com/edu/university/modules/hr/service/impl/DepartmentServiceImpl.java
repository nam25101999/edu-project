package com.edu.university.modules.hr.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.hr.dto.request.DepartmentRequestDTO;
import com.edu.university.modules.hr.dto.response.DepartmentResponseDTO;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.mapper.DepartmentMapper;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO requestDTO) {
        if (departmentRepository.existsByCode(requestDTO.getCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã phòng ban đã tồn tại");
        }
        Department department = departmentMapper.toEntity(requestDTO);
        department.setActive(true);
        return departmentMapper.toResponseDTO(departmentRepository.save(department));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponseDTO> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable)
                .map(departmentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng ban"));
        return departmentMapper.toResponseDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentByCode(String code) {
        Department department = departmentRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng ban"));
        return departmentMapper.toResponseDTO(department);
    }

    @Override
    @Transactional
    public DepartmentResponseDTO updateDepartment(UUID id, DepartmentRequestDTO requestDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng ban"));
        departmentMapper.updateEntityFromDTO(requestDTO, department);
        return departmentMapper.toResponseDTO(departmentRepository.save(department));
    }

    @Override
    @Transactional
    public void deleteDepartment(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng ban"));
        department.softDelete("system");
        departmentRepository.save(department);
    }
}
