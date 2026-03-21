package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MajorRepository extends JpaRepository<Major, UUID> {

    boolean existsByMajorCode(String majorCode);

    List<Major> findByFaculty_Id(UUID facultyId);

}