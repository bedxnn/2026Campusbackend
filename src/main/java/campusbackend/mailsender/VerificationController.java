package campusbackend.mailsender;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
