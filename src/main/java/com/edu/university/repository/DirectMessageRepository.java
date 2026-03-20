package com.edu.university.repository;

import com.edu.university.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, UUID> {

    // Lấy lịch sử chat giữa 2 người (Sender và Receiver có thể đảo chiều)
    @Query("SELECT m FROM DirectMessage m WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) " +
            "OR (m.sender.id = :user2 AND m.receiver.id = :user1) ORDER BY m.sentAt ASC")
    List<DirectMessage> findConversation(@Param("user1") UUID user1, @Param("user2") UUID user2);

    // Lấy tin nhắn chưa đọc của một người
    List<DirectMessage> findByReceiverIdAndIsReadFalse(UUID receiverId);
}