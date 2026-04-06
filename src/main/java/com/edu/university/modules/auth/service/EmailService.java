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
 * Service xá»­ lÃ½ viá»‡c gá»­i Email thÃ´ng bÃ¡o vÃ  mÃ£ xÃ¡c thá»±c (OTP).
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
            message.setSubject("[University Portal] MÃ£ xÃ¡c thá»±c báº£o máº­t");

            String content = String.format(
                    "ChÃ o báº¡n,\n\n" +
                            "Há»‡ thá»‘ng vá»«a nháº­n Ä‘Æ°á»£c yÃªu cáº§u cáº§n xÃ¡c thá»±c tá»« tÃ i khoáº£n cá»§a báº¡n.\n" +
                            "MÃ£ OTP báº£o máº­t cá»§a báº¡n lÃ : %s\n\n" +
                            "LÆ°u Ã½ quan trá»ng:\n" +
                            "- MÃ£ nÃ y cÃ³ hiá»‡u lá»±c trong vÃ²ng 5 phÃºt.\n" +
                            "- Tuyá»‡t Ä‘á»‘i KHÃ”NG chia sáº» mÃ£ nÃ y hoáº·c chuyá»ƒn tiáº¿p email nÃ y cho báº¥t ká»³ ai, ká»ƒ cáº£ nhÃ¢n viÃªn quáº£n trá»‹.\n\n" +
                            "TrÃ¢n trá»ng,\nBan Quáº£n Trá»‹ Há»‡ Thá»‘ng.",
                    otp
            );

            message.setText(content);
            mailSender.send(message);

            log.info("ÄÃ£ gá»­i OTP thÃ nh cÃ´ng tá»›i email: {}", to);

        } catch (Exception e) {
            log.error("Lá»—i há»‡ thá»‘ng khi gá»­i email tá»›i {}: {}", to, e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "KhÃ´ng thá»ƒ káº¿t ná»‘i Ä‘áº¿n mÃ¡y chá»§ gá»­i email. Vui lÃ²ng thá»­ láº¡i sau.");
        }
    }
}
