package campusbackend.websocket;

import campusbackend.auth.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {

    List<Messages> findBySender(Users sender);
    List<Messages> findByReceiver(Users receiver);
    List<Messages> findBySenderAndReceiver(Users sender, Users receiver);
    List<Messages> findByReceiverAndIsReadFalse(Users receiver);
    long countByReceiverAndIsReadFalse(Users receiver);

    @Query("SELECT m FROM Messages m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Messages> findConversation(@Param("user1") Users user1, @Param("user2") Users user2);

    // FIXED VERSION - No more CASE statement
    @Query("SELECT DISTINCT m.receiver FROM Messages m WHERE m.sender = :user " +
            "UNION " +
            "SELECT DISTINCT m.sender FROM Messages m WHERE m.receiver = :user")
    List<Users> findConversationPartners(@Param("user") Users user);
}