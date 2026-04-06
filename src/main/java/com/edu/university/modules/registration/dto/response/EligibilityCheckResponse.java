package com.edu.university.modules.registration.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EligibilityCheckResponse {
    private boolean isEligible;
    private int totalCredits;
    private List<Violation> violations;

    @Data
    @Builder
    public static class Violation {
        private UUID courseSectionId;
        private String reason; // SCHEDULE_CONFLICT, PREREQUISITE_NOT_MET, MAX_CREDITS_EXCEEDED
        private String message;
        private UUID conflictingWithId;
    }
}
