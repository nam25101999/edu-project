package com.edu.university.modules.community.controller;

import com.edu.university.modules.community.dto.CommentRequest;
import com.edu.university.modules.community.dto.MessageRequest;
import com.edu.university.modules.community.dto.TopicRequest;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.community.service.ChatForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ChatForumController {

    private final ChatForumService communityService;

    // === 1. API DIỄN ĐÀN (FORUM) ===

    @GetMapping("/forums/general")
    public ResponseEntity<?> getGeneralTopics() {
        return ResponseEntity.ok(communityService.getGeneralTopics());
    }

    @GetMapping("/forums/course/{courseId}")
    public ResponseEntity<?> getCourseTopics(@PathVariable UUID courseId) {
        return ResponseEntity.ok(communityService.getCourseTopics(courseId));
    }

    @PostMapping("/forums/topics")
    public ResponseEntity<?> createTopic(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Valid @RequestBody TopicRequest request) {
        return ResponseEntity.ok(communityService.createTopic(userDetails.getId(), request));
    }

    @GetMapping("/forums/topics/{topicId}/comments")
    public ResponseEntity<?> getComments(@PathVariable UUID topicId) {
        return ResponseEntity.ok(communityService.getComments(topicId));
    }

    @PostMapping("/forums/topics/{topicId}/comments")
    public ResponseEntity<?> addComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable UUID topicId,
                                        @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(communityService.addComment(userDetails.getId(), topicId, request));
    }

    // === 2. API TIN NHẮN TRỰC TIẾP (CHAT) ===

    @PostMapping("/chat/send")
    public ResponseEntity<?> sendMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(communityService.sendMessage(userDetails.getId(), request));
    }

    @GetMapping("/chat/conversation/{partnerId}")
    public ResponseEntity<?> getConversation(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable UUID partnerId) {
        return ResponseEntity.ok(communityService.getConversation(userDetails.getId(), partnerId));
    }

    @GetMapping("/chat/unread")
    public ResponseEntity<?> getUnreadMessages(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(communityService.getUnreadMessages(userDetails.getId()));
    }
}