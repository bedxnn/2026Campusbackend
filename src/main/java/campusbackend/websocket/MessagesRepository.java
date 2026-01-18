package campusbackend.websocket;



import campusbackend.auth.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {

    // Find all messages sent by a specific user
    List<Messages> findBySender(Users sender);

    // Find all messages received by a specific user
    List<Messages> findByReceiver(Users receiver);

    // Find messages between two users (one direction)
    List<Messages> findBySenderAndReceiver(Users sender, Users receiver);

    // Find all unread messages for a user
    List<Messages> findByReceiverAndIsReadFalse(Users receiver);

    // Count unread messages for a user
    long countByReceiverAndIsReadFalse(Users receiver);

    // Find conversation between two users (both directions)
    @Query("SELECT m FROM Messages m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<Messages> findConversation(@Param("user1") Users user1, @Param("user2") Users user2);

    // Get all unique conversations for a user
    @Query("SELECT DISTINCT CASE " +
            "WHEN m.sender = :user THEN m.receiver " +
            "ELSE m.sender END " +
            "FROM Messages m WHERE m.sender = :user OR m.receiver = :user")
    List<Users> findConversationPartners(@Param("user") Users user);
}