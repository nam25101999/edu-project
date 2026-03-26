package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MajorRepository extends JpaRepository<Major, UUID> {

    List<Major> findByFaculty_Id(UUID facultyId);
    boolean existsByMajorCode(String majorCode);
    // Spring Data JPA đã có sẵn findById(UUID id) trả về Optional<Major>
}