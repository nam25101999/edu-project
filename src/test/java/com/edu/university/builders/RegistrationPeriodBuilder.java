package com.edu.university.builders;

import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import java.time.LocalDateTime;
import java.util.UUID;

public class RegistrationPeriodBuilder {
    private String name = "Registration Period " + UUID.randomUUID().toString().substring(0, 5);
    private Semester semester;
    private LocalDateTime startTime = LocalDateTime.now().minusDays(1);
    private LocalDateTime endTime = LocalDateTime.now().plusDays(10);

    public static RegistrationPeriodBuilder aRegistrationPeriod() {
        return new RegistrationPeriodBuilder();
    }

    public RegistrationPeriodBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public RegistrationPeriodBuilder withSemester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public RegistrationPeriod build() {
        return RegistrationPeriod.builder()
                .name(name)
                .semester(semester)
                .startTime(startTime)
                .endTime(endTime)
                .isActive(true)
                .build();
    }
}
