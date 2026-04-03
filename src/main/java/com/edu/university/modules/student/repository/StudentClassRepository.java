package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, UUID> {

    // Lọc danh sách lớp theo cả khoa và ngành (API: GET /api/student-classes?department_id={}&major_id={})
    List<StudentClass> findByDepartmentIdAndMajorId(UUID departmentId, UUID majorId);

    // Lọc danh sách lớp chỉ theo khoa
    List<StudentClass> findByDepartmentId(UUID departmentId);

    // Lọc danh sách lớp chỉ theo ngành
    List<StudentClass> findByMajorId(UUID majorId);

    // Kiểm tra mã lớp đã tồn tại chưa
    boolean existsByClassCode(String classCode);
}