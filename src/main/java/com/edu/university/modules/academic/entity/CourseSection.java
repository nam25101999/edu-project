package com.edu.university.modules.academic.entity;

import com.edu.university.common.entity.BaseEntity;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.Major;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SQLDelete(sql = "UPDATE course_sections SET is_active = false, deleted_at = NOW() WHERE id = ?")
@Where(clause = "is_active = true")
public class CourseSection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String sectionCode;

    @Column(name = "class_code", length = 50)
    private String classCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    // Giảng viên chính phụ trách
    @Column(name = "employee_id")
    private UUID lecturerId;

    // Phòng học, tòa nhà
    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "building_id")
    private UUID buildingId;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "min_students")
    private Integer minStudents;

    @Column(name = "class_type", length = 50)
    private String classType;

    @Column(length = 50)
    private String status;

    @Column(name = "registration_start")
    private LocalDateTime registrationStart;

    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;

    @Column(length = 255)
    private String note;

    @Column(name = "is_system", columnDefinition = "bit default 0")
    @Builder.Default
    private Boolean isSystem = false;

    @PrePersist
    public void prePersist() {
        if (this.sectionCode == null) {
            this.sectionCode = "SEC_" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}