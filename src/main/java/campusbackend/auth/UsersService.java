package campusbackend.auth;

import org.apache.catalina.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    private final UserServiceRepository userServiceRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UserServiceRepository userServiceRepository,PasswordEncoder passwordEncoder){
        this.userServiceRepository=userServiceRepository;
        this.passwordEncoder=passwordEncoder;
    }

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
        user.setEnabled(false);
        userServiceRepository.save(user);
    }

    public void login(String email,String password){
        Users user = userServiceRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("email don't exist"));

        if(!passwordEncoder.matches(password, user.getPassword())){
         throw new RuntimeException("wrong password");
}
        if(!user.isEnabled()){
        throw new RuntimeException("Verify your account");
    }


        }
    }


