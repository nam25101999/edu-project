package com.edu.university;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.community.dto.CommentRequest;
import com.edu.university.modules.community.dto.MessageRequest;
import com.edu.university.modules.community.dto.TopicRequest;
import com.edu.university.modules.community.entity.DirectMessage;
import com.edu.university.modules.community.entity.ForumComment;
import com.edu.university.modules.community.entity.ForumTopic;
import com.edu.university.modules.community.service.ChatForumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatForumService chatForumService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl mockUserDetails;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        // Giả lập thông tin User đang đăng nhập (AuthenticationPrincipal)
        currentUserId = UUID.randomUUID();
        mockUserDetails = UserDetailsImpl.builder()
                .id(currentUserId)
                .username("test_student")
                .password("password123")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_STUDENT")))
                .build();
    }

    // ==========================================
    // 1. TEST DIỄN ĐÀN (FORUM)
    // ==========================================

    @Test
    void testGetGeneralTopics_ShouldReturn200() throws Exception {
        ForumTopic mockTopic = ForumTopic.builder()
                .id(UUID.randomUUID())
                .title("Thông báo nghỉ Tết")
                .content("Lịch nghỉ tết âm lịch...")
                .createdAt(LocalDateTime.now())
                .build();

        when(chatForumService.getGeneralTopics()).thenReturn(List.of(mockTopic));

        mockMvc.perform(get("/api/community/forums/general")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Thông báo nghỉ Tết"));
    }

    @Test
    void testCreateTopic_ShouldReturn200() throws Exception {
        TopicRequest request = new TopicRequest("Hỏi bài tập", "Giúp mình bài số 1", null);
        ForumTopic mockTopic = ForumTopic.builder()
                .id(UUID.randomUUID())
                .title("Hỏi bài tập")
                .content("Giúp mình bài số 1")
                .build();

        when(chatForumService.createTopic(eq(currentUserId), any(TopicRequest.class))).thenReturn(mockTopic);

        mockMvc.perform(post("/api/community/forums/topics")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hỏi bài tập"));
    }

    @Test
    void testAddComment_ShouldReturn200() throws Exception {
        UUID topicId = UUID.randomUUID();
        CommentRequest request = new CommentRequest("Cảm ơn bạn nhé!");
        ForumComment mockComment = ForumComment.builder()
                .id(UUID.randomUUID())
                .content("Cảm ơn bạn nhé!")
                .build();

        when(chatForumService.addComment(eq(currentUserId), eq(topicId), any(CommentRequest.class)))
                .thenReturn(mockComment);

        mockMvc.perform(post("/api/community/forums/topics/{topicId}/comments", topicId)
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Cảm ơn bạn nhé!"));
    }

    // ==========================================
    // 2. TEST TIN NHẮN (CHAT)
    // ==========================================

    @Test
    void testSendMessage_ShouldReturn200() throws Exception {
        UUID partnerId = UUID.randomUUID();
        MessageRequest request = new MessageRequest(partnerId, "Thầy ơi cho em hỏi xíu ạ.");
        DirectMessage mockMessage = DirectMessage.builder()
                .id(UUID.randomUUID())
                .content("Thầy ơi cho em hỏi xíu ạ.")
                .isRead(false)
                .build();

        when(chatForumService.sendMessage(eq(currentUserId), any(MessageRequest.class))).thenReturn(mockMessage);

        mockMvc.perform(post("/api/community/chat/send")
                        .with(user(mockUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Thầy ơi cho em hỏi xíu ạ."))
                .andExpect(jsonPath("$.read").value(false)); // Ánh xạ từ isRead
    }

    @Test
    void testGetConversation_ShouldReturn200() throws Exception {
        UUID partnerId = UUID.randomUUID();
        DirectMessage mockMessage = DirectMessage.builder()
                .id(UUID.randomUUID())
                .content("Xin chào em!")
                .isRead(true)
                .build();

        when(chatForumService.getConversation(currentUserId, partnerId)).thenReturn(List.of(mockMessage));

        mockMvc.perform(get("/api/community/chat/conversation/{partnerId}", partnerId)
                        .with(user(mockUserDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Xin chào em!"));
    }
}