package com.edu.university.modules.curriculum.repository;

import com.edu.university.modules.curriculum.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByCourseCode(String courseCode);
    boolean existsByCourseCode(String courseCode);

    @Query(
            value = """
                    select c
                    from Course c
                    left join fetch c.department d
                    where (:search is null
                        or lower(c.courseCode) like lower(concat('%', :search, '%'))
                        or lower(c.name) like lower(concat('%', :search, '%'))
                        or lower(coalesce(c.courseNameEn, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(d.name, '')) like lower(concat('%', :search, '%')))
                    """,
            countQuery = """
                    select count(c)
                    from Course c
                    left join c.department d
                    where (:search is null
                        or lower(c.courseCode) like lower(concat('%', :search, '%'))
                        or lower(c.name) like lower(concat('%', :search, '%'))
                        or lower(coalesce(c.courseNameEn, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(d.name, '')) like lower(concat('%', :search, '%')))
                    """
    )
    Page<Course> findPageWithDepartment(@Param("search") String search, Pageable pageable);

    @Query("""
            select c
            from Course c
            left join fetch c.department
            where c.id = :id
            """)
    Optional<Course> findDetailById(@Param("id") UUID id);
}
