package com.edu.university.modules.registration.dto.request;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class EligibilityCheckRequest {
    private UUID studentId;
    private List<UUID> courseSectionIds;
    private UUID registrationPeriodId;
}
