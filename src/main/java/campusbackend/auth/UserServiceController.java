package campusbackend.auth;

import campusbackend.dto.LoginDto;
import campusbackend.dto.SignupDto;
import campusbackend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserServiceController {
    private final UsersService usersService;
    private final campusbackend.Items.AuthService authService;
    private final JwtService jwtService;  // Add this

    public UserServiceController(UsersService usersService,
                                 campusbackend.Items.AuthService authService,
                                 JwtService jwtService) {  // Add this
        this.usersService = usersService;
        this.authService = authService;
        this.jwtService = jwtService;  // Add this
    }

    @PostMapping("/Signup")
    public ResponseEntity<String> Signup(@RequestBody SignupDto dto){
        usersService.signup(dto.getEmail(), dto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body("signed up");
    }

    @PostMapping("/Login")
    public ResponseEntity<?> Login(@RequestBody LoginDto dto){
        // Authenticate user (your existing logic)
        usersService.login(dto.getEmail(), dto.getPassword());

        // Generate BOTH tokens
        String accessToken = jwtService.generateAccessToken(dto.getEmail());
        String refreshToken = jwtService.generateRefreshToken(dto.getEmail());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Refresh token required"));
        }


        if (jwtService.isTokenValid(refreshToken) && jwtService.isRefreshToken(refreshToken)) {
            String email = jwtService.extractEmail(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(email);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Invalid or expired refresh token"));
    }

    @GetMapping("/api/auth/current-user")
    public ResponseEntity<Users> getCurrentUser(@RequestHeader("Authorization") String token) {
        Users user = authService.getCurrentUser(token);
        return ResponseEntity.ok(user);
    }
}