package campusbackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserServiceRepository extends JpaRepository<Users,Long> {
    boolean existsByEmail(String email);
    Optional <Users>findByEmail(String email);
}
