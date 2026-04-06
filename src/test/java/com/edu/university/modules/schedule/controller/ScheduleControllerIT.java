package com.edu.university.modules.schedule.controller;
 
import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
 
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
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
 
    private CourseSection courseSection;
    private Users lecturer;
    private Room room;
    private Building building;
    private Schedule schedule;
 
    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        courseSectionRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();
        buildingRepository.deleteAll();
 
        courseSection = courseSectionRepository.save(CourseSection.builder()
                .classCode("CS101.01").isActive(true).build());
 
        lecturer = userRepository.save(Users.builder()
                .username("lecturer1").email("l1@edu.com").password("pass").isActive(true).build());
 
        building = buildingRepository.save(Building.builder()
                .buildingCode("B1").buildingName("Building 1").isActive(true).build());
 
        room = roomRepository.save(Room.builder()
                .roomCode("R101").roomName("Room 101").building(building).isActive(true).build());
 
        schedule = scheduleRepository.save(Schedule.builder()
                .courseSection(courseSection)
                .lecturer(lecturer)
                .room(room)
                .dayOfWeek(2) // Thứ 2
                .shift("Sáng")
                .startPeriod(1)
                .endPeriod(3)
                .date(LocalDate.now())
                .isActive(true)
                .build());
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchedule_Success() throws Exception {
        ScheduleRequestDTO request = new ScheduleRequestDTO();
        request.setCourseSectionId(courseSection.getId());
        request.setLecturerId(lecturer.getId());
        request.setRoomId(room.getId());
        request.setDayOfWeek(3);
        request.setShift("Chiều");
        request.setStartPeriod(7);
        request.setEndPeriod(9);
        request.setDate(LocalDate.now().plusDays(1));
 
        mockMvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek").value(3))
                .andExpect(jsonPath("$.shift").value("Chiều"));
    }
 
    @Test
    @WithMockUser(roles = "USER")
    void getAllSchedules_Success() throws Exception {
        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
 
    @Test
    @WithMockUser(roles = "USER")
    void getScheduleById_Success() throws Exception {
        mockMvc.perform(get("/api/schedules/{id}", schedule.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek").value(2));
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSchedule_Success() throws Exception {
        ScheduleRequestDTO request = new ScheduleRequestDTO();
        request.setCourseSectionId(courseSection.getId());
        request.setLecturerId(lecturer.getId());
        request.setRoomId(room.getId());
        request.setDayOfWeek(4);
        request.setShift("Sáng");
        request.setDate(schedule.getDate());
 
        mockMvc.perform(put("/api/schedules/{id}", schedule.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek").value(4));
    }
 
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSchedule_Success() throws Exception {
        mockMvc.perform(delete("/api/schedules/{id}", schedule.getId()))
                .andExpect(status().isNoContent());
 
        entityManager.flush();
        entityManager.clear();
 
        mockMvc.perform(get("/api/schedules/{id}", schedule.getId()))
                .andExpect(status().isNotFound());
    }
}
