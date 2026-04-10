package com.edu.university.modules.academic.service.impl;

import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AcademicCalendarStructureService {

    public static final int ACADEMIC_START_YEAR = 2000;

    private final AcademicYearRepository academicYearRepository;
    private final SemesterRepository semesterRepository;

    @Transactional
    public void ensureStandardStructure() {
        int currentYear = LocalDate.now().getYear();
        for (int year = ACADEMIC_START_YEAR; year <= currentYear + 1; year++) {
            final int startYear = year;
            AcademicYear academicYear = academicYearRepository.findByAcademicCode(formatAcademicCode(startYear))
                    .orElseGet(() -> academicYearRepository.save(AcademicYear.builder()
                            .academicCode(formatAcademicCode(startYear))
                            .academicName("Năm học " + formatAcademicCode(startYear))
                            .academicYear(formatAcademicCode(startYear))
                            .startDate(LocalDate.of(startYear, 9, 1))
                            .endDate(LocalDate.of(startYear + 1, 8, 31))
                            .isActive(true)
                            .build()));

            ensureSemesterExists(academicYear, "HK1-" + shortYear(startYear), "Học kỳ 1",
                    LocalDate.of(startYear, 9, 1), LocalDate.of(startYear + 1, 1, 31));
            ensureSemesterExists(academicYear, "HK2-" + shortYear(startYear), "Học kỳ 2",
                    LocalDate.of(startYear + 1, 2, 1), LocalDate.of(startYear + 1, 6, 30));
            ensureSemesterExists(academicYear, "HK3-" + shortYear(startYear), "Học kỳ hè",
                    LocalDate.of(startYear + 1, 7, 1), LocalDate.of(startYear + 1, 8, 31));
        }
    }

    private void ensureSemesterExists(AcademicYear academicYear, String code, String name,
            LocalDate defaultStartDate, LocalDate defaultEndDate) {
        semesterRepository.findBySemesterCode(code).ifPresentOrElse(existingSemester -> {
            boolean shouldUpdate = false;

            if (existingSemester.getAcademicYear() == null) {
                existingSemester.setAcademicYear(academicYear);
                shouldUpdate = true;
            }
            if (existingSemester.getSemesterName() == null || existingSemester.getSemesterName().isBlank()) {
                existingSemester.setSemesterName(name);
                shouldUpdate = true;
            }
            if (existingSemester.getStartDate() == null) {
                existingSemester.setStartDate(defaultStartDate);
                shouldUpdate = true;
            }
            if (existingSemester.getEndDate() == null) {
                existingSemester.setEndDate(defaultEndDate);
                shouldUpdate = true;
            }
            if (!existingSemester.isActive()) {
                existingSemester.setIsActive(true);
                shouldUpdate = true;
            }

            if (shouldUpdate) {
                semesterRepository.save(existingSemester);
            }
        }, () -> semesterRepository.save(Semester.builder()
                .semesterCode(code)
                .semesterName(name)
                .academicYear(academicYear)
                .startDate(defaultStartDate)
                .endDate(defaultEndDate)
                .isActive(true)
                .build()));
    }

    private String formatAcademicCode(int year) {
        return year + "-" + (year + 1);
    }

    private String shortYear(int year) {
        return String.valueOf(year).substring(2);
    }
}
