package com.edu.university.modules.enrollment.repository.course.repository;

import com.edu.university.modules.enrollment.repository.course.entity.ClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID> {
}