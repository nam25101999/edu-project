package com.edu.university.modules.community.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.community.dto.CommentRequest;
import com.edu.university.modules.community.dto.MessageRequest;
import com.edu.university.modules.community.dto.TopicRequest;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.community.entity.DirectMessage;
import com.edu.university.modules.community.entity.ForumComment;
import com.edu.university.modules.community.entity.ForumTopic;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.course.repository.CourseRepository;
import com.edu.university.modules.community.repository.DirectMessageRepository;
import com.edu.university.modules.community.repository.ForumCommentRepository;
import com.edu.university.modules.community.repository.ForumTopicRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service xử lý các nghiệp vụ Diễn đàn và Chat trực tiếp.
 * Đã chuẩn hóa lỗi theo Enterprise Standard (ErrorCode).
 */
@Service
@RequiredArgsConstructor
public class ChatForumService {

    private final ForumTopicRepository topicRepo;
    private final ForumCommentRepository commentRepo;
    private final DirectMessageRepository messageRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;

    // ==================== FORUM / DIỄN ĐÀN ====================

    @LogAction(action = "CREATE_TOPIC", entityName = "FORUM_TOPIC")
    @Transactional
    public ForumTopic createTopic(UUID authorId, TopicRequest request) {
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Course course = null;
        if (request.courseId() != null) {
            course = courseRepo.findById(request.courseId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        }

        ForumTopic topic = ForumTopic.builder()
                .title(request.title())
                .content(request.content())
                .author(author)
                .course(course)
                .createdAt(LocalDateTime.now())
                .build();

        return topicRepo.save(topic);
    }

    @LogAction(action = "VIEW_GENERAL_TOPICS", entityName = "FORUM_TOPIC")
    public List<ForumTopic> getGeneralTopics() {
        return topicRepo.findByCourseIsNullOrderByCreatedAtDesc();
    }

    @LogAction(action = "VIEW_COURSE_TOPICS", entityName = "FORUM_TOPIC")
    public List<ForumTopic> getCourseTopics(UUID courseId) {
        // Kiểm tra môn học tồn tại trước khi lấy topic
        if (!courseRepo.existsById(courseId)) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
        }
        return topicRepo.findByCourseIdOrderByCreatedAtDesc(courseId);
    }

    @LogAction(action = "ADD_COMMENT", entityName = "FORUM_COMMENT")
    @Transactional
    public ForumComment addComment(UUID authorId, UUID topicId, CommentRequest request) {
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ForumTopic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOPIC_NOT_FOUND));

        ForumComment comment = ForumComment.builder()
                .topic(topic)
                .author(author)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepo.save(comment);
    }

    @LogAction(action = "VIEW_COMMENTS", entityName = "FORUM_COMMENT")
    public List<ForumComment> getComments(UUID topicId) {
        if (!topicRepo.existsById(topicId)) {
            throw new BusinessException(ErrorCode.TOPIC_NOT_FOUND);
        }
        return commentRepo.findByTopicIdOrderByCreatedAtAsc(topicId);
    }

    // ==================== CHAT / TIN NHẮN TRỰC TIẾP ====================

    @LogAction(action = "SEND_MESSAGE", entityName = "DIRECT_MESSAGE")
    @Transactional
    public DirectMessage sendMessage(UUID senderId, MessageRequest request) {
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User receiver = userRepo.findById(request.receiverId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_CHAT));

        DirectMessage message = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.content())
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build();

        return messageRepo.save(message);
    }

    @LogAction(action = "VIEW_CONVERSATION", entityName = "DIRECT_MESSAGE")
    @Transactional
    public List<DirectMessage> getConversation(UUID currentUser, UUID partnerUser) {
        if (!userRepo.existsById(partnerUser)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND_CHAT);
        }

        List<DirectMessage> messages = messageRepo.findConversation(currentUser, partnerUser);

        // Đánh dấu các tin nhắn người kia gửi cho mình là "Đã đọc"
        messages.stream()
                .filter(msg -> msg.getReceiver().getId().equals(currentUser) && !msg.isRead())
                .forEach(msg -> {
                    msg.setRead(true);
                    messageRepo.save(msg);
                });

        return messages;
    }

    @LogAction(action = "VIEW_UNREAD_MESSAGES", entityName = "DIRECT_MESSAGE")
    public List<DirectMessage> getUnreadMessages(UUID userId) {
        return messageRepo.findByReceiverIdAndIsReadFalse(userId);
    }
}