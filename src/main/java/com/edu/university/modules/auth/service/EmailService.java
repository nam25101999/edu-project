package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.annotation.LogAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service xử lý việc gửi Email thông báo và mã xác thực (OTP).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @LogAction(action = "SEND_OTP_EMAIL", entityName = "USER")
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("[University Portal] Mã xác thực bảo mật");

            String content = String.format(
                    "Chào bạn,\n\n" +
                            "Hệ thống vừa nhận được yêu cầu cần xác thực từ tài khoản của bạn.\n" +
                            "Mã OTP bảo mật của bạn là: %s\n\n" +
                            "Lưu ý quan trọng:\n" +
                            "- Mã này có hiệu lực trong vòng 5 phút.\n" +
                            "- Tuyệt đối KHÔNG chia sẻ mã này hoặc chuyển tiếp email này cho bất kỳ ai, kể cả nhân viên quản trị.\n\n" +
                            "Trân trọng,\nBan Quản Trị Hệ Thống.",
                    otp
            );

            message.setText(content);
            mailSender.send(message);

            log.info("Đã gửi OTP thành công tới email: {}", to);

        } catch (Exception e) {
            log.error("Lỗi hệ thống khi gửi email tới {}: {}", to, e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể kết nối đến máy chủ gửi email. Vui lòng thử lại sau.");
        }
    }
}