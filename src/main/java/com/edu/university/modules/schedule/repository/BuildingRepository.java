package com.edu.university.modules.schedule.repository;

import com.edu.university.modules.schedule.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<Building, UUID> {
    Optional<Building> findByBuildingCode(String buildingCode);
    boolean existsByBuildingCode(String buildingCode);
}
