package campusbackend.mailsender;

import campusbackend.auth.Users;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Users user;

    private String token;

    private LocalDateTime expiryDate;

    public Verification() {

    }

    public Verification(Users user, String token) {
        this.user = user;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
