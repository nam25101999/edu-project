package com.edu.university.modules.curriculum.repository;

import com.edu.university.modules.curriculum.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MajorRepository extends JpaRepository<Major, UUID> {
    Optional<Major> findByMajorCode(String majorCode);
    boolean existsByMajorCode(String majorCode);
    java.util.List<Major> findByDepartmentId(java.util.UUID departmentId);

    @Query(
            value = """
                    select m
                    from Major m
                    left join fetch m.department d
                    left join fetch m.faculty f
                    where (:search is null
                        or lower(m.majorCode) like lower(concat('%', :search, '%'))
                        or lower(m.name) like lower(concat('%', :search, '%'))
                        or lower(coalesce(d.name, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(f.name, '')) like lower(concat('%', :search, '%')))
                    """,
            countQuery = """
                    select count(m)
                    from Major m
                    left join m.department d
                    left join m.faculty f
                    where (:search is null
                        or lower(m.majorCode) like lower(concat('%', :search, '%'))
                        or lower(m.name) like lower(concat('%', :search, '%'))
                        or lower(coalesce(d.name, '')) like lower(concat('%', :search, '%'))
                        or lower(coalesce(f.name, '')) like lower(concat('%', :search, '%')))
                    """
    )
    Page<Major> findPageWithRelations(@Param("search") String search, Pageable pageable);

    @Query("""
            select m
            from Major m
            left join fetch m.department
            left join fetch m.faculty
            where m.id = :id
            """)
    Optional<Major> findDetailById(@Param("id") UUID id);
}
