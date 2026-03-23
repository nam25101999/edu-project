package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service xử lý việc gửi Email thông báo và mã xác thực (OTP).
 * Sử dụng JavaMailSender để kết nối với SMTP server cấu hình trong application.properties.
 * Đã chuẩn hóa việc xử lý lỗi theo ErrorCode hệ thống.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Gửi email chứa mã OTP cho người dùng yêu cầu khôi phục mật khẩu.
     * * @param to Địa chỉ email người nhận.
     * @param otp Mã số xác thực gồm 6 chữ số.
     * @throws BusinessException Nếu có lỗi xảy ra trong quá trình gửi mail (SYS_500).
     */
    @LogAction(action = "SEND_OTP_EMAIL", entityName = "USER")
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("[University Portal] Mã xác thực khôi phục mật khẩu");

            String content = String.format(
                    "Chào bạn,\n\n" +
                            "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với email này.\n" +
                            "Mã OTP của bạn là: %s\n\n" +
                            "Lưu ý:\n" +
                            "- Mã này có hiệu lực trong vòng 5 phút.\n" +
                            "- Tuyệt đối không chia sẻ mã này với bất kỳ ai.\n\n" +
                            "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email và đổi mật khẩu để bảo mật tài khoản.\n\n" +
                            "Trân trọng,\nBan Quản Trị Hệ Thống.",
                    otp
            );

            message.setText(content);

            mailSender.send(message);
            log.info("Đã gửi OTP thành công tới email: {}", to);

        } catch (Exception e) {
            log.error("Lỗi hệ thống khi gửi email tới {}: {}", to, e.getMessage());
            // Ném lỗi BusinessException với mã hệ thống SYS_500 để GlobalExceptionHandler xử lý
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể gửi email xác thực. Vui lòng thử lại sau.");
        }
    }
}