package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {
    Page<Survey> findByIsActiveTrue(Pageable pageable);
}
