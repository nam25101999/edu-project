package com.edu.university.modules.schedule.repository;

import com.edu.university.modules.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByCourseSectionId(UUID courseSectionId);
    List<Schedule> findByRoomId(UUID roomId);
    List<Schedule> findByLecturerId(UUID lecturerId);
}
