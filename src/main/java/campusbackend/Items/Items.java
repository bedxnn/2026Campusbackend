package campusbackend.Items;

import campusbackend.auth.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Users user;

    private String idNumber;

    private String studentName;

    private LocalDateTime createdAt;

    public Items(Users user){
        this.user=user;
        this.createdAt=LocalDateTime.now();
    }
    public Items(){

    }

    public String getIdNumber(){
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;

    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setstudentName(String studentName) {
        this.studentName = studentName;
    }
}
