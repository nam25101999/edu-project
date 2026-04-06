package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.StudentClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, UUID> {

    // Lọc danh sách lớp theo cả khoa và ngành
    Page<StudentClass> findByDepartmentIdAndMajorId(UUID departmentId, UUID majorId, Pageable pageable);

    // Lọc danh sách lớp chỉ theo khoa
    Page<StudentClass> findByDepartmentId(UUID departmentId, Pageable pageable);

    // Lọc danh sách lớp chỉ theo ngành
    Page<StudentClass> findByMajorId(UUID majorId, Pageable pageable);

    // Kiểm tra mã lớp đã tồn tại chưa
    boolean existsByClassCode(String classCode);
}