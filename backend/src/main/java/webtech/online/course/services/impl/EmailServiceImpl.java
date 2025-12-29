package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webtech.online.course.models.User;
import webtech.online.course.models.VerificationToken;
import webtech.online.course.services.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    @Override
    @Async
    public void sendSimpleMessage(User user, VerificationToken token) {
        String subject= "Xác minh tài khoản của bạn";
        String verifyURL = "http://localhost:8081/api/auth/verify?token=" + token.getToken();

        String content = """
            Xin chào %s,
    
            Cảm ơn bạn đã đăng ký! 
            Hãy nhấp vào liên kết bên dưới để kích hoạt tài khoản của bạn:
            %s
    
            Liên kết này sẽ hết hạn sau 24 giờ.
    
            Trân trọng,
            Hệ thống của chúng tôi.
        """.formatted(user.getFullName(), verifyURL);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nguyenkhoe2652004@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
    }

    @Override
    @Async
    public void sendCustomMessage(User user, String msg, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nguyenkhoe2652004@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(msg);
        emailSender.send(message);
    }
}
