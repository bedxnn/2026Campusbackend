package campusbackend.auth;

import campusbackend.dto.LoginDto;
import campusbackend.dto.SignupDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserServiceController {
    private final UsersService usersService;
    private final campusbackend.Items.AuthService authService;  // Add this

    public UserServiceController(UsersService usersService, campusbackend.Items.AuthService authService) {
        this.usersService = usersService;
        this.authService = authService;  // Add this
    }

    @PostMapping("/Signup")
    public ResponseEntity<String> Signup(@RequestBody SignupDto dto){
        usersService.signup(dto.getEmail(), dto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body("signed up");
    }

    @PostMapping("/Login")
    public ResponseEntity<?> Login(@RequestBody LoginDto dto){
        String token = usersService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(Map.of(
                "token", token
        ));
    }

    // ADD THIS NEW ENDPOINT
    @GetMapping("/api/auth/current-user")
    public ResponseEntity<Users> getCurrentUser(@RequestHeader("Authorization") String token) {
        Users user = authService.getCurrentUser(token);
        return ResponseEntity.ok(user);
    }
}