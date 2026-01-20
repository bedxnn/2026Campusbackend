package campusbackend.auth;

import campusbackend.dto.LoginDto;
import campusbackend.dto.SignupDto;
import campusbackend.ratelimit.RateLimitService;
import campusbackend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
public class UserServiceController {
    private final UsersService usersService;
    private final campusbackend.Items.AuthService authService;
    private final JwtService jwtService;
    private final RateLimitService rateLimitService;

    public UserServiceController(UsersService usersService,
                                 campusbackend.Items.AuthService authService,
                                 JwtService jwtService,
                                 RateLimitService rateLimitService) {
        this.usersService = usersService;
        this.authService = authService;
        this.jwtService = jwtService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/Signup")
    public ResponseEntity<?> Signup(@RequestBody SignupDto dto, HttpServletRequest request){
        // Rate limit: 3 signups per IP per hour
        String clientIp = getClientIP(request);
        String rateLimitKey = "signup_" + clientIp;

        if (!rateLimitService.allowRequest(rateLimitKey, 3, Duration.ofHours(1))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many signup attempts. Please try again later."));
        }

        usersService.signup(dto.getEmail(), dto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body("signed up");
    }

    @PostMapping("/Login")
    public ResponseEntity<?> Login(@RequestBody LoginDto dto){
        // Rate limit: 5 login attempts per 15 minutes per email
        String rateLimitKey = "login_" + dto.getEmail();

        if (!rateLimitService.allowRequest(rateLimitKey, 5, Duration.ofMinutes(15))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many login attempts. Please try again in 15 minutes."));
        }

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

        // Extract email first to use for rate limiting
        String email;
        try {
            email = jwtService.extractEmail(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid refresh token"));
        }

        // Rate limit: 10 refresh attempts per hour per user
        String rateLimitKey = "refresh_" + email;

        if (!rateLimitService.allowRequest(rateLimitKey, 10, Duration.ofHours(1))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Too many token refresh attempts. Please try again later."));
        }

        if (jwtService.isTokenValid(refreshToken) && jwtService.isRefreshToken(refreshToken)) {
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

    /**
     * Extract client IP address, considering proxy headers
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}