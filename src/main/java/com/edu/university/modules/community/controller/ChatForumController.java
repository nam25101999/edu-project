package com.edu.university.modules.community.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.community.dto.CommentRequest;
import com.edu.university.modules.community.dto.MessageRequest;
import com.edu.university.modules.community.dto.TopicRequest;
import com.edu.university.modules.community.entity.DirectMessage;
import com.edu.university.modules.community.entity.ForumComment;
import com.edu.university.modules.community.entity.ForumTopic;
import com.edu.university.modules.community.service.ChatForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller xử lý các API cho Diễn đàn và Chat.
 * Đã cập nhật để trả về ApiResponse chuẩn hóa đồng bộ với ChatForumService.
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ChatForumController {

    private final ChatForumService communityService;

    // === 1. API DIỄN ĐÀN (FORUM) ===

    @GetMapping("/forums/general")
    public ApiResponse<List<ForumTopic>> getGeneralTopics() {
        return ApiResponse.success(communityService.getGeneralTopics());
    }

    @GetMapping("/forums/course/{courseId}")
    public ApiResponse<List<ForumTopic>> getCourseTopics(@PathVariable UUID courseId) {
        return ApiResponse.success(communityService.getCourseTopics(courseId));
    }

    @PostMapping("/forums/topics")
    public ApiResponse<ForumTopic> createTopic(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @Valid @RequestBody TopicRequest request) {
        return ApiResponse.created("Tạo chủ đề thảo luận thành công",
                communityService.createTopic(userDetails.getId(), request));
    }

    @GetMapping("/forums/topics/{topicId}/comments")
    public ApiResponse<List<ForumComment>> getComments(@PathVariable UUID topicId) {
        return ApiResponse.success(communityService.getComments(topicId));
    }

    @PostMapping("/forums/topics/{topicId}/comments")
    public ApiResponse<ForumComment> addComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @PathVariable UUID topicId,
                                                @Valid @RequestBody CommentRequest request) {
        return ApiResponse.created("Đã gửi bình luận",
                communityService.addComment(userDetails.getId(), topicId, request));
    }

    // === 2. API TIN NHẮN TRỰC TIẾP (CHAT) ===

    @PostMapping("/chat/send")
    public ApiResponse<DirectMessage> sendMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @Valid @RequestBody MessageRequest request) {
        return ApiResponse.created("Gửi tin nhắn thành công",
                communityService.sendMessage(userDetails.getId(), request));
    }

    @GetMapping("/chat/conversation/{partnerId}")
    public ApiResponse<List<DirectMessage>> getConversation(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @PathVariable UUID partnerId) {
        return ApiResponse.success(communityService.getConversation(userDetails.getId(), partnerId));
    }

    @GetMapping("/chat/unread")
    public ApiResponse<List<DirectMessage>> getUnreadMessages(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(communityService.getUnreadMessages(userDetails.getId()));
    }
}