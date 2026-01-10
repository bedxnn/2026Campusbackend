package campusbackend.mailsender;

import campusbackend.auth.UserServiceRepository;
import campusbackend.auth.Users;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class VerificationService {
    private final EmailService emailService;
    private final EmailVerificationRepository repo;
    private final SecureRandom random = new SecureRandom();
    private final UserServiceRepository userServiceRepository;

    public VerificationService(EmailService emailService, EmailVerificationRepository repo,UserServiceRepository userServiceRepository){
        this.emailService=emailService ;
        this.repo = repo;
        this.userServiceRepository=userServiceRepository;
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

}
