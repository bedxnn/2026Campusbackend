package campusbackend.auth;

import campusbackend.mailsender.VerificationService;
import campusbackend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsersService {
    private final UserServiceRepository userServiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final JwtService jwtService;

    public UsersService(UserServiceRepository userServiceRepository,PasswordEncoder passwordEncoder,VerificationService verificationService,JwtService jwtService){
        this.userServiceRepository=userServiceRepository;
        this.passwordEncoder=passwordEncoder;
        this.verificationService=verificationService;
        this.jwtService=jwtService;
    }
@Transactional
    public void signup(String email,String password){
       String hashedPassword = passwordEncoder.encode(password);
        if (userServiceRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(hashedPassword);

     user.setEnabled(true);


    userServiceRepository.save(user);
        verificationService.sendCode(email);
    }
    public String login(String email, String password) {
        Users user = userServiceRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("email don't exist"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("wrong password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Verify your account");
        }

        return jwtService.generateToken(user.getEmail());
    }

        }



