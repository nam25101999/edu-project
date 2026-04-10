package com.edu.university.modules.schedule.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScheduleControllerIT extends BaseIntegrationTest {

        @Autowired
        private ScheduleRepository scheduleRepository;
        @Autowired
        private CourseSectionRepository courseSectionRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private RoomRepository roomRepository;
        @Autowired
        private BuildingRepository buildingRepository;
        @Autowired
        private CourseRepository courseRepository;
        @Autowired
        private SemesterRepository semesterRepository;

        private CourseSection testSection;
        private Users testLecturer;
        private Room testRoom;

        @BeforeEach
        void setUp() {
                scheduleRepository.deleteAll();

                testLecturer = userRepository.save(Users.builder()
                                .username("lecturer_sch")
                                .password("password")
                                .email("sch_lec@edu.vn")
                                .isActive(true)
                                .build());

                Course course = courseRepository.save(Course.builder()
                                .courseCode("SCH101")
                                .name("Schedule Course")
                                .credits(new BigDecimal("3"))
                                .isActive(true)
                                .build());

                Semester semester = semesterRepository.save(Semester.builder()
                                .semesterName("HK1 2023-2024")
                                .semesterCode("20231_SCH")
                                .build());

                testSection = courseSectionRepository.save(CourseSection.builder()
                                .sectionCode("SCH101_01")
                                .course(course)
                                .semester(semester)
                                .capacity(30)
                                .isActive(true)
                                .build());

                Building building = buildingRepository.save(Building.builder()
                                .buildingName("Building A")
                                .buildingCode("BLD_A")
                                .isActive(true)
                                .build());

                testRoom = roomRepository.save(Room.builder()
                                .roomCode("A101")
                                .building(building)
                                .capacity(40)
                                .isActive(true)
                                .build());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void create_ShouldReturn201_WhenValid() throws Exception {
                ScheduleRequestDTO request = new ScheduleRequestDTO();
                request.setCourseSectionId(testSection.getId());
                request.setLecturerId(testLecturer.getId());
                request.setRoomId(testRoom.getId());
                request.setDayOfWeek(2); // Monday
                request.setStartPeriod(1);
                request.setEndPeriod(3);

                mockMvc.perform(post("/api/schedules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.dayOfWeek").value(2));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAll_ShouldReturnPage() throws Exception {
                Schedule schedule = Schedule.builder()
                                .courseSection(testSection)
                                .lecturer(testLecturer)
                                .room(testRoom)
                                .dayOfWeek(3)
                                .startPeriod(4)
                                .endPeriod(6)
                                .isActive(true)
                                .build();
                scheduleRepository.save(schedule);

                mockMvc.perform(get("/api/schedules"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));
        }
}
