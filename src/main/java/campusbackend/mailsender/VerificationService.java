package campusbackend.mailsender;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class VerificationService {
    private final EmailService emailService;
    String name = "mike";
    private final EmailVerificationRepository repo;
    private final SecureRandom random = new SecureRandom();

    public VerificationService(EmailService emailService, EmailVerificationRepository repo){
        this.emailService=emailService ;
        this.repo = repo;
    }
    public void sendCode(String email){
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

        if(v.isUsed()) return false;
        if (Instant.now().isAfter(v.getExpiresAt())) return false;
        if(!v.getCode().equals(code)) return false;

        v.setUsed(true);
        repo.save(v);

        return true;
    }

}
