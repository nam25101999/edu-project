package com.edu.university.modules.schedule.repository;

import com.edu.university.modules.schedule.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    Optional<TimeSlot> findBySlotCode(String slotCode);
    boolean existsBySlotCode(String slotCode);
}
