package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}
