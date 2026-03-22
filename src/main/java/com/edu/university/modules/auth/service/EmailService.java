package com.edu.university.modules.auth.service;

import com.edu.university.modules.report.annotation.LogAction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @LogAction(action = "SEND_OTP_EMAIL", entityName = "USER")
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Yêu cầu khôi phục mật khẩu - University Portal");
        message.setText("Chào bạn,\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu. Mã OTP của bạn là: " + otp + "\n\n" +
                "Mã này sẽ hết hạn trong vòng 5 phút.\n" +
                "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\nBan Quản Trị Hệ Thống.");

        mailSender.send(message);
    }
}