package com.edu.university.modules.schedule.repository;

import com.edu.university.modules.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    Page<Schedule> findByCourseSectionId(UUID courseSectionId, Pageable pageable);
    List<Schedule> findAllByCourseSectionId(UUID courseSectionId);
    Page<Schedule> findByRoomId(UUID roomId, Pageable pageable);
    List<Schedule> findAllByRoomId(UUID roomId);
    Page<Schedule> findByLecturerId(UUID lecturerId, Pageable pageable);
    List<Schedule> findAllByLecturerId(UUID lecturerId);
}
