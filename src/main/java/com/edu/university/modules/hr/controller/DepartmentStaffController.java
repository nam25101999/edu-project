package com.edu.university.modules.hr.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.hr.dto.response.EmployeeResponseDTO;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.entity.Position;
import com.edu.university.modules.hr.mapper.EmployeeMapper;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentStaffController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    @Data
    @Builder
    public static class DepartmentStaffResponse {
        private EmployeeResponseDTO dean;
        private List<EmployeeResponseDTO> viceDeans;
        private List<EmployeeResponseDTO> lecturers;
    }

    @Data
    public static class AssignStaffRequest {
        private UUID userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PotentialLecturerResponse {
        private UUID userId;
        private String username;
        private String fullName;
        private String email;
    }

    @GetMapping("/staff/potential-lecturers")
    @Transactional(readOnly = true)
    public ResponseEntity<BaseResponse<PageResponse<PotentialLecturerResponse>>> getPotentialLecturers(
            @PageableDefault(size = 50) Pageable pageable) {
        Page<PotentialLecturerResponse> page = userRepository.findByRoles_Name("LECTURER", pageable)
                .map(u -> {
                    // Try to find employee record for name
                    String fullName = employeeRepository.findByUserId(u.getId())
                            .map(Employee::getFullName)
                            .orElse(u.getUsername());
                            
                    return PotentialLecturerResponse.builder()
                        .userId(u.getId())
                        .username(u.getUsername())
                        .fullName(fullName)
                        .email(u.getEmail())
                        .build();
                });
        return ResponseEntity.ok(BaseResponse.ok(PageResponse.of(page)));
    }

    @GetMapping("/{id}/staff")
    @Transactional(readOnly = true)
    public ResponseEntity<BaseResponse<DepartmentStaffResponse>> getStaff(@PathVariable UUID id) {
        Department dept = departmentRepository.findById(id).orElseThrow();
        
        List<Employee> allStaff = employeeRepository.findByDepartmentId(id);
        
        Employee dean = allStaff.stream()
                .filter(e -> e.getUser() != null && e.getPosition() != null && "TRUONG_KHOA".equals(e.getPosition().getCode()))
                .findFirst().orElse(null);
                
        List<Employee> viceDeans = allStaff.stream()
                .filter(e -> e.getUser() != null && e.getPosition() != null && "PHO_KHOA".equals(e.getPosition().getCode()))
                .collect(Collectors.toList());
                
        List<Employee> lecturers = allStaff.stream()
                .filter(e -> e.getUser() != null && e.getPosition() != null && "GIANG_VIEN".equals(e.getPosition().getCode()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(BaseResponse.ok(DepartmentStaffResponse.builder()
                .dean(dean != null ? employeeMapper.toResponseDTO(dean) : null)
                .viceDeans(viceDeans.stream().map(employeeMapper::toResponseDTO).collect(Collectors.toList()))
                .lecturers(lecturers.stream().map(employeeMapper::toResponseDTO).collect(Collectors.toList()))
                .build()));
    }

    @PostMapping("/{id}/staff/assign-dean")
    @Transactional
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> assignDean(@PathVariable UUID id, @RequestBody AssignStaffRequest request) {
        return assignRole(id, request.getUserId(), "TRUONG_KHOA", true);
    }

    @PostMapping("/{id}/staff/add-vice-dean")
    @Transactional
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> addViceDean(@PathVariable UUID id, @RequestBody AssignStaffRequest request) {
        return assignRole(id, request.getUserId(), "PHO_KHOA", false);
    }

    @PostMapping("/{id}/staff/add-lecturer")
    @Transactional
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> addLecturer(@PathVariable UUID id, @RequestBody AssignStaffRequest request) {
        return assignRole(id, request.getUserId(), "GIANG_VIEN", false);
    }

    @DeleteMapping("/{id}/staff/{employeeId}")
    @Transactional
    public ResponseEntity<BaseResponse<Void>> removeStaff(@PathVariable UUID id, @PathVariable UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        employee.setDepartment(null);
        employee.setPosition(null);
        employeeRepository.save(employee);
        return ResponseEntity.ok(BaseResponse.ok("Đã xóa nhân sự khỏi khoa", null));
    }

    private ResponseEntity<BaseResponse<EmployeeResponseDTO>> assignRole(UUID deptId, UUID userId, String posCode, boolean isUnique) {
        Department dept = departmentRepository.findById(deptId).orElseThrow();
        Users user = userRepository.findById(userId).orElseThrow();
        
        // Strict role validation
        boolean isLecturer = user.getRoles().stream()
                .anyMatch(r -> "LECTURER".equals(r.getName()));
        if (!isLecturer) {
            throw new com.edu.university.common.exception.BusinessException(ErrorCode.FORBIDDEN, "Người dùng phải có quyền GIẢNG VIÊN (LECTURER) để thực hiện thao tác này");
        }
        
        Position pos = positionRepository.findByCode(posCode)
                .orElseGet(() -> {
                    Position newPos = Position.builder()
                            .code(posCode)
                            .name(posCode.replace("_", " "))
                            .isActive(true)
                            .build();
                    return positionRepository.save(newPos);
                });

        if (isUnique) {
            // Remove existing unique role holder (e.g. Dean)
            employeeRepository.findByDepartmentIdAndPositionCode(deptId, posCode).ifPresent(e -> {
                e.setPosition(null);
                e.setDepartment(null);
                employeeRepository.save(e);
            });
        }

        Employee employee = employeeRepository.findByUserId(userId)
                .orElseGet(() -> Employee.builder()
                        .user(user)
                        .fullName(user.getUsername()) // Default
                        .employeeCode("EMP-" + user.getUsername())
                        .isActive(true)
                        .build());

        employee.setDepartment(dept);
        employee.setPosition(pos);
        
        return ResponseEntity.ok(BaseResponse.ok("Phân công nhiệm vụ thành công", employeeMapper.toResponseDTO(employeeRepository.save(employee))));
    }
}
