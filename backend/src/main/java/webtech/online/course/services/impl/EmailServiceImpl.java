package webtech.online.course.services.impl;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webtech.online.course.models.User;
import webtech.online.course.models.VerificationToken;
import webtech.online.course.services.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final Resend resend;

    @Value("${resend.from:onboarding@resend.dev}")
    private String fromEmail;

    @Override
    @Async
    public void sendSimpleMessage(User user, VerificationToken token) {
        String subject = "Xác minh tài khoản của bạn";
        String verifyURL = "https://backend-lms-h8c4.onrender.com/api/auth/verify?token=" + token.getToken();

        String htmlContent = """
                    <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                        <h2>Xin chào %s,</h2>
                        <p>Cảm ơn bạn đã đăng ký! Hãy nhấp vào liên kết bên dưới để kích hoạt tài khoản của bạn:</p>
                        <p><a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">Kích hoạt tài khoản</a></p>
                        <p>Hoặc copy đường link này: %s</p>
                        <p>Liên kết này sẽ hết hạn sau 24 giờ.</p>
                        <hr/>
                        <p>Trân trọng,<br/>Hệ thống của chúng tôi.</p>
                    </div>
                """
                .formatted(user.getFullName(), verifyURL, verifyURL);

        CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(user.getEmail())
                .subject(subject)
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(sendEmailRequest);
            log.info("Email sent successfully: {}", data.getId());
        } catch (Exception e) {
            log.error("Failed to send email via Resend: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendCustomMessage(User user, String msg, String subject) {
        CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(user.getEmail())
                .subject(subject)
                .html("<div style='white-space: pre-wrap;'>" + msg + "</div>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(sendEmailRequest);
            log.info("Custom email sent successfully: {}", data.getId());
        } catch (Exception e) {
            log.error("Failed to send custom email via Resend: {}", e.getMessage());
        }
    }
}
