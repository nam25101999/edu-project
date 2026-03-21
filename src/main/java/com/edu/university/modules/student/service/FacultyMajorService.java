package com.edu.university.modules.student.service;

import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.student.dto.FacultyMajorDtos.*;
import com.edu.university.modules.student.entity.Faculty;
import com.edu.university.modules.student.entity.Major;
import com.edu.university.modules.student.repository.FacultyRepository;
import com.edu.university.modules.student.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacultyMajorService {

    private final FacultyRepository facultyRepo;
    private final MajorRepository majorRepo;

    // --- FACULTY CRUD ---
    public List<Faculty> getAllFaculties() {
        return facultyRepo.findAll();
    }

    @LogAction(action = "CREATE_FACULTY", entityName = "FACULTY")
    @Transactional
    public Faculty createFaculty(FacultyRequest request) {
        if (facultyRepo.existsByFacultyCode(request.facultyCode())) {
            throw new RuntimeException("Mã khoa đã tồn tại");
        }
        Faculty faculty = Faculty.builder()
                .facultyCode(request.facultyCode())
                .name(request.name())
                .description(request.description())
                .contactEmail(request.contactEmail())
                .build();
        return facultyRepo.save(faculty);
    }

    // --- MAJOR CRUD ---
    public List<Major> getAllMajors() {
        return majorRepo.findAll();
    }

    public List<Major> getMajorsByFaculty(UUID facultyId) {
        return majorRepo.findByFaculty_Id(facultyId);
    }

    @LogAction(action = "CREATE_MAJOR", entityName = "MAJOR")
    @Transactional
    public Major createMajor(MajorRequest request) {
        if (majorRepo.existsByMajorCode(request.majorCode())) {
            throw new RuntimeException("Mã ngành đã tồn tại");
        }
        Faculty faculty = facultyRepo.findById(request.facultyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khoa"));

        Major major = Major.builder()
                .majorCode(request.majorCode())
                .name(request.name())
                .faculty(faculty)
                .build();
        return majorRepo.save(major);
    }
}