package com.edu.university.modules.curriculum.repository;

import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingProgramCourseRepository extends JpaRepository<TrainingProgramCourse, UUID> {
    List<TrainingProgramCourse> findByTrainingProgramId(UUID trainingProgramId);
    List<TrainingProgramCourse> findByCourseId(UUID courseId);

    @Query(
            value = """
                    select tpc
                    from TrainingProgramCourse tpc
                    left join fetch tpc.trainingProgram tp
                    left join fetch tpc.course c
                    left join fetch tpc.prerequisiteCourse pc
                    where (:trainingProgramId is null or tp.id = :trainingProgramId)
                    """,
            countQuery = """
                    select count(tpc)
                    from TrainingProgramCourse tpc
                    left join tpc.trainingProgram tp
                    where (:trainingProgramId is null or tp.id = :trainingProgramId)
                    """
    )
    Page<TrainingProgramCourse> findPageWithRelations(@Param("trainingProgramId") UUID trainingProgramId, Pageable pageable);

    @Query("""
            select tpc
            from TrainingProgramCourse tpc
            left join fetch tpc.trainingProgram
            left join fetch tpc.course
            left join fetch tpc.prerequisiteCourse
            where tpc.id = :id
            """)
    Optional<TrainingProgramCourse> findDetailById(@Param("id") UUID id);
}
