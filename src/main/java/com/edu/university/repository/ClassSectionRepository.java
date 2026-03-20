package com.edu.university.repository;

import com.edu.university.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID> {
}