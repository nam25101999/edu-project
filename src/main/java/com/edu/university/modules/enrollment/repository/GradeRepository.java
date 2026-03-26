package com.edu.university.modules.enrollment.repository;

import com.edu.university.modules.enrollment.dto.GradeFullDto;
import com.edu.university.modules.enrollment.dto.GradeGpaDto;
import com.edu.university.modules.enrollment.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {

    Optional<Grade> findByEnrollmentId(UUID enrollmentId);

    // Phương thức phục vụ tính GPA và điều kiện tiên quyết
    @Query("""
        SELECT new com.edu.university.modules.enrollment.dto.GradeGpaDto(
            c.id,
            g.gpaScore,
            g.totalScore,
            c.credits
        )
        FROM Grade g
        JOIN g.enrollment e
        JOIN e.classSection cs
        JOIN cs.course c
        WHERE e.student.id = :studentId
    """)
    List<GradeGpaDto> findGpaData(@Param("studentId") UUID studentId);

    // Có thể dùng chung findGpaData hoặc tạo alias để code rõ nghĩa hơn
    default List<GradeGpaDto> findGradeForPrerequisite(UUID studentId) {
        return findGpaData(studentId);
    }

    @Query("""
        SELECT new com.edu.university.modules.enrollment.dto.GradeFullDto(
            cs.semester,
            cs.year,
            c.courseCode,
            c.name,
            c.credits,
            g.attendanceScore,
            g.midtermScore,
            g.finalScore,
            g.totalScore,
            g.letterGrade,
            g.gpaScore
        )
        FROM Grade g
        JOIN g.enrollment e
        JOIN e.classSection cs
        JOIN cs.course c
        JOIN e.student s
        WHERE s.id = :studentId
    """)
    List<GradeFullDto> findFullGradeData(@Param("studentId") UUID studentId);
}