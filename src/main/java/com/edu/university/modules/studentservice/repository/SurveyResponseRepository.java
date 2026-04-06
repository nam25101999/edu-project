package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, UUID> {
    List<SurveyResponse> findBySurveyId(UUID surveyId);
    List<SurveyResponse> findByStudentId(UUID studentId);
}
