package campusbackend.Items;

import campusbackend.auth.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ItemsRepository extends JpaRepository<Items,Long> {
    List<Items> findByUser(Users user);

    List<Items> findAllByOrderByCreatedAtDesc();

    List<Items> findByStudentNameIgnoreCaseOrIdNumberIgnoreCase(String studentName, String studentId);

    // Search by ID number
    List<Items> findByIdNumberContainingIgnoreCase(String idNumber);
}
