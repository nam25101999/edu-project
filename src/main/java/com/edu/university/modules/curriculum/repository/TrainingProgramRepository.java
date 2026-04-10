package com.edu.university.modules.curriculum.repository;

import com.edu.university.modules.curriculum.entity.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, UUID> {
    Optional<TrainingProgram> findByProgramCode(String programCode);
    boolean existsByProgramCode(String programCode);
    java.util.List<TrainingProgram> findByMajorId(UUID majorId);

    @Query(
            value = """
                    select tp
                    from TrainingProgram tp
                    left join fetch tp.major m
                    left join fetch tp.department d
                    where (:search is null
                        or lower(tp.programCode) like lower(concat('%', :search, '%'))
                        or lower(tp.programName) like lower(concat('%', :search, '%'))
                        or lower(coalesce(tp.programNameEn, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(m.name, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(d.name, '')) like lower(concat('%', :search, '%')))
                    """,
            countQuery = """
                    select count(tp)
                    from TrainingProgram tp
                    left join tp.major m
                    left join tp.department d
                    where (:search is null
                        or lower(tp.programCode) like lower(concat('%', :search, '%'))
                        or lower(tp.programName) like lower(concat('%', :search, '%'))
                        or lower(coalesce(tp.programNameEn, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(m.name, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(d.name, '')) like lower(concat('%', :search, '%')))
                    """
    )
    Page<TrainingProgram> findPageWithRelations(@Param("search") String search, Pageable pageable);

    @Query("""
            select tp
            from TrainingProgram tp
            left join fetch tp.major
            left join fetch tp.department
            where tp.id = :id
            """)
    Optional<TrainingProgram> findDetailById(@Param("id") UUID id);
}
