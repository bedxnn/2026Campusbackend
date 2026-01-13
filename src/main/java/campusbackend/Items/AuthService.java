package campusbackend.Items;

import campusbackend.auth.UserServiceRepository;
import campusbackend.auth.Users;
import campusbackend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserServiceRepository userRepository;

    public Users getCurrentUser(String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email).orElseThrow();
    }
}