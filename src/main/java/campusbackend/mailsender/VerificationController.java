package campusbackend.mailsender;

import campusbackend.dto.ForgotPasswordRequest;
import campusbackend.dto.ResetPasswordRequest;
import campusbackend.dto.VerificationRequest;
import campusbackend.ratelimit.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class VerificationController {

    private final VerificationService verificationService;
    private final RateLimitService rateLimitService;


    public VerificationController(VerificationService verificationService,RateLimitService rateLimitService){
        this.verificationService = verificationService;
        this.rateLimitService=rateLimitService;
    }


    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationRequest request){
        try{
            verificationService.verifyCode(request.getEmail(), request.getCode());
            return ResponseEntity.ok("account verified");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("bad input");

        }

    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestBody ForgotPasswordRequest request) {
        try {
            verificationService.resendCode(request.getEmail());
            return ResponseEntity.ok("Verification code resent");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Too many")) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(e.getMessage());
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        verificationService.resetPassword(resetPasswordRequest.getToken(),resetPasswordRequest.getNewPassword());
        return  ResponseEntity.ok("Password reset succesfully");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        if (!rateLimitService.allowRequest(request.getEmail())) {
            return ResponseEntity.status(429).body("Too many requests. Try again later.");
        }
        verificationService.generateResetToken(request.getEmail());
        return ResponseEntity.ok("Password reset email sent");
    }

}
