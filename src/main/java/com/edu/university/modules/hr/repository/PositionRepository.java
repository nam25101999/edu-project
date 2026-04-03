package com.edu.university.modules.hr.repository;

import com.edu.university.modules.hr.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    Optional<Position> findByCode(String code);

    Optional<Position> findByCodeAndIsActiveTrue(String code);

    boolean existsByCode(String code);
}
