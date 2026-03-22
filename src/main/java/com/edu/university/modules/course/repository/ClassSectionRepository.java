package com.edu.university.modules.course.repository;

import com.edu.university.modules.course.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID> {
}