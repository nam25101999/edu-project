package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, UUID> {
    List<Survey> findByIsActiveTrue();
}
