package com.edu.university.modules.examination.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamRoom;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamRoomRepository;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExamRoomControllerIT extends BaseIntegrationTest {

    @Autowired
    private ExamRoomRepository examRoomRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private ExamTypeRepository examTypeRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private RoomRepository roomRepository;

    private Exam testExam;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        ExamType examType = examTypeRepository.save(ExamType.builder()
                .name("Room Test Type")
                .description("Room description")
                .isActive(true)
                .build());

        Course course = courseRepository.save(Course.builder()
                .courseCode("EXAM_ROOM_101")
                .name("Exam Room Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("Hoc ky kiem thu phong thi")
                .semesterCode("SEM_EXAM_ROOM")
                .build());

        testExam = examRepository.save(Exam.builder()
                .examType(examType)
                .courseClass(course)
                .semester(semester)
                .examDate(LocalDate.now().plusDays(10))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .isActive(true)
                .build());

        Building building = buildingRepository.save(Building.builder()
                .buildingCode("BROOM01")
                .buildingName("Toa thi")
                .totalFloors(5)
                .isActive(true)
                .build());

        testRoom = roomRepository.save(Room.builder()
                .roomCode("P101")
                .roomName("Phong 101")
                .building(building)
                .floor(1)
                .capacity(60)
                .roomType("CLASSROOM")
                .status("AVAILABLE")
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnCreatedPayload_WhenValidRequest() throws Exception {
        ExamRoomRequestDTO request = new ExamRoomRequestDTO();
        request.setExamId(testExam.getId());
        request.setRoomId(testRoom.getId());
        request.setCapacity(45);

        mockMvc.perform(post("/api/exam-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.examId").value(testExam.getId().toString()))
                .andExpect(jsonPath("$.data.roomId").value(testRoom.getId().toString()))
                .andExpect(jsonPath("$.data.capacity").value(45));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByExamId_ShouldReturnPageOfRooms() throws Exception {
        examRoomRepository.save(ExamRoom.builder()
                .exam(testExam)
                .room(testRoom)
                .capacity(50)
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/exam-rooms/exam/{examId}", testExam.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].roomName").value("Phong 101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldSoftDeleteExamRoom() throws Exception {
        ExamRoom saved = examRoomRepository.save(ExamRoom.builder()
                .exam(testExam)
                .room(testRoom)
                .capacity(30)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/exam-rooms/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));

        entityManager.flush();
        entityManager.clear();

        assertFalse(examRoomRepository.findById(saved.getId()).isPresent());
    }
}
