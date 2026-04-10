package com.edu.university.common.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Main Data Seeder Orchestrator.
 * Finds all beans implementing ModuleSeeder and executes them in order.
 */
@Component
@org.springframework.context.annotation.Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final List<ModuleSeeder> seeders;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("===== START MODULAR SEED DATA =====");

        // Sort seeders by their defined order
        seeders.stream()
                .sorted(Comparator.comparingInt(ModuleSeeder::getOrder))
                .forEach(seeder -> {
                    try {
                        seeder.seed();
                    } catch (Exception e) {
                        log.error("Error executing seeder {}: {}", seeder.getClass().getSimpleName(), e.getMessage(), e);
                    }
                });

        log.info("===== END MODULAR SEED DATA =====");
    }
}
