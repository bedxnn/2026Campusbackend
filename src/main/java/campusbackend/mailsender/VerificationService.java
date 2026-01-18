package campusbackend.mailsender;

import campusbackend.auth.UserServiceRepository;
import campusbackend.auth.Users;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class VerificationService {
    private final EmailService emailService;
    private final EmailVerificationRepository repo;
    private final SecureRandom random = new SecureRandom();
    private final UserServiceRepository userServiceRepository;
    private final VerificationRepository verificationRepository;
    private final PasswordEncoder passwordEncoder;

    public VerificationService(EmailService emailService, EmailVerificationRepository repo,UserServiceRepository userServiceRepository,VerificationRepository verificationRepository,PasswordEncoder passwordEncoder){
        this.emailService=emailService ;
        this.repo = repo;
        this.passwordEncoder=passwordEncoder;
        this.userServiceRepository=userServiceRepository;
        this.verificationRepository=verificationRepository;
    }
    public void sendCode(String email){

        repo.deleteByEmailAndUsed(email, false);

        String code = String.format("%06d", random.nextInt(1_000_000));

        EmailVerification v = new EmailVerification();
        v.setEmail(email);
        v.setCode(code);
        v.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
        v.setUsed(false);
        repo.save(v);

        emailService.sendEmail(email,code);//ver code sewnd
    }
    public boolean verifyCode(String email,String code){
        EmailVerification v = repo.findTopByEmailOrderByIdDesc(email).orElseThrow(() -> new RuntimeException("no code sent"));

        if (Instant.now().isAfter(v.getExpiresAt())) {
            throw new RuntimeException("Code expired");
        }

        if (v.isUsed()) {
            throw new RuntimeException("Code already used");
        }

        if (!v.getCode().equals(code)) {
            throw new RuntimeException("Invalid code");
        }
        v.setUsed(true);
        repo.save(v);

        Users user = userServiceRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userServiceRepository.save(user);


        return true;
    }
    public String generateResetToken(String email){
        Users user = userServiceRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("user not found"));

        String token = UUID.randomUUID().toString();

        Verification resetToken = new Verification();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationRepository.save(resetToken);


        String resetLink = "http://localhost:5173/reset-password?token=" + token + "&email=" + email;
        String message = "Click the link below to reset your password:\n\n" +
                resetLink +
                "\n\nThis link will expire in 1 hour.\n\n" +
                "If you didn't request this, please ignore this email.";

        emailService.sendPasswordRequestToken(email, message);

        return token;
    }
    public void resetPassword(String token,String newPassword){
        Verification verification = verificationRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        if( verification.isExpired()){
            throw new RuntimeException("Token is expired");

        }

        Users user = verification.getUser();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
         userServiceRepository.save(user);
        verificationRepository.delete(verification);
    }

    public void resendCode(String email) {

        Instant since = Instant.now().minus(25, ChronoUnit.MINUTES);
        long recentSends = repo.countByEmailAndExpiresAtAfter(email, since);

        if (recentSends >= 5) {
            throw new RuntimeException("Too many verification code requests. Please try again in 25 minutes.");
        }


        repo.deleteByEmailAndUsed(email, false);

        String code = String.format("%06d", random.nextInt(1_000_000));

        EmailVerification v = new EmailVerification();
        v.setEmail(email);
        v.setCode(code);
        v.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
        v.setUsed(false);
        repo.save(v);

        emailService.sendEmail(email, code);
    }

}
