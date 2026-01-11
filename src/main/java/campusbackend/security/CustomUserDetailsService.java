package campusbackend.security;

import campusbackend.auth.UserServiceRepository;
import campusbackend.auth.Users;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceRepository userServiceRepository;

    public CustomUserDetailsService(UserServiceRepository userServiceRepository) {
        this.userServiceRepository = userServiceRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userServiceRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .accountExpired(false)
                .credentialsExpired(false)
                .authorities("USER")
                .build();
    }
}
