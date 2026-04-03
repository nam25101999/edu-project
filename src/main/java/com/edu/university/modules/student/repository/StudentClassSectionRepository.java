package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.StudentClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentClassSectionRepository extends JpaRepository<StudentClassSection, UUID> {

    List<StudentClassSection> findByStudentId(UUID studentId);

    // Đổi thành số ít: studentClassId
    List<StudentClassSection> findByStudentClassId(UUID studentClassId);

    // Đổi thành số ít: studentClassId
    List<StudentClassSection> findByStudentClassIdAndIsActiveTrue(UUID studentClassId);

    // Đổi thành số ít: studentClassId
    boolean existsByStudentIdAndStudentClassId(UUID studentId, UUID studentClassId);
}