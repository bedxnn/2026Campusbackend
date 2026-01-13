package campusbackend.auth;

import campusbackend.dto.LoginDto;
import campusbackend.dto.SignupDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserServiceController {
    private final UsersService usersService;

    public UserServiceController(UsersService usersService) {
        this.usersService = usersService;
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
}
