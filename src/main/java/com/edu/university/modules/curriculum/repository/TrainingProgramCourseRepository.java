package com.edu.university.modules.curriculum.repository;

import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingProgramCourseRepository extends JpaRepository<TrainingProgramCourse, UUID> {
    List<TrainingProgramCourse> findByTrainingProgramId(UUID trainingProgramId);
    List<TrainingProgramCourse> findByCourseId(UUID courseId);
}
