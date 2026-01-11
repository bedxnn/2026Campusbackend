package campusbackend.mailsender;

import campusbackend.dto.ResetPasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VerificationController {

    private final VerificationService verificationService;


    public VerificationController(VerificationService verificationService){
        this.verificationService = verificationService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode (@RequestParam String email){
        verificationService.sendCode(email);
        return ResponseEntity.ok("code sent");
    }
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code){
        try{
            verificationService.verifyCode(email, code);
            return ResponseEntity.ok("account verified");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("bad input");

        }

    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        verificationService.resetPassword(resetPasswordRequest.getNewPassword(),resetPasswordRequest.getToken());
        return  ResponseEntity.ok("Password reset succesfully");
    }
}
