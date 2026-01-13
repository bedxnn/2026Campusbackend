package campusbackend.mailsender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);

    @Query("SELECT COUNT(e) FROM EmailVerification e WHERE e.email = :email AND e.expiresAt > :since")
    long countByEmailAndExpiresAtAfter(@Param("email") String email, @Param("since") Instant since);

    @Transactional
    @Modifying
    void deleteByEmailAndUsed(String email, boolean used);
}
