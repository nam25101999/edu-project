package com.edu.university.common.seeder;

/**
 * Interface for module-specific data seeders.
 */
public interface ModuleSeeder {
    /**
     * Executes the seeding logic for the module.
     */
    void seed();

    /**
     * Determines the execution order. 
     * Lower values run first.
     */
    int getOrder();
}
