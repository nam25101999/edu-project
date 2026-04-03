package com.edu.university.modules.registration.repository;

import com.edu.university.modules.registration.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, UUID> {
    List<RegistrationPeriod> findBySemesterId(UUID semesterId);
}
