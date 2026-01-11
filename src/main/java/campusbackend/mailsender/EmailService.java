package campusbackend.mailsender;

import campusbackend.auth.UserServiceRepository;
import campusbackend.auth.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;


    private final JavaMailSender mailSender;



    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;

    }

    public void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Your verification code");
        message.setText(code);

        mailSender.send(message);
    }


    public void forgotPassword(String to, String resetToken ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("reset your password");

        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;

        message.setText("Click the link below to reset your password"+ resetLink);

        mailSender.send(message);
    }
}

