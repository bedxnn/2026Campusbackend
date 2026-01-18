package campusbackend.dto;



import campusbackend.auth.UserServiceRepository;
import campusbackend.auth.Users;
import campusbackend.Items.AuthService;
import campusbackend.dto.SendMessageRequest;
import campusbackend.websocket.Messages;
import campusbackend.websocket.MessagesService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MessageWebSocketController {

    private final MessagesService messagesService;
    private final AuthService authService;
    private final UserServiceRepository usersRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageWebSocketController(
            MessagesService messagesService,
            AuthService authService,
            UserServiceRepository usersRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.messagesService = messagesService;
        this.authService = authService;
        this.usersRepository = usersRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // WebSocket endpoint for sending messages
    @MessageMapping("/send")
    public void sendMessage(SendMessageRequest request,
                            @Header("Authorization") String token) {
        try {
            Users sender = authService.getCurrentUser(token);
            Users receiver = usersRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            Messages message = messagesService.sendMessage(sender, receiver, request.getContent());

            // Send to specific user (receiver)
            messagingTemplate.convertAndSendToUser(
                    receiver.getEmail(),
                    "/queue/messages",
                    message
            );

            // Also send confirmation back to sender
            messagingTemplate.convertAndSendToUser(
                    sender.getEmail(),
                    "/queue/messages",
                    message
            );
        } catch (Exception e) {
            // Handle error - you might want to send error message back to client
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    // REST endpoint to get conversation history
    @GetMapping("/api/messages/conversation/{userId}")
    public ResponseEntity<List<Messages>> getConversation(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {

        Users currentUser = authService.getCurrentUser(token);
        Users otherUser = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Messages> conversation = messagesService.getConversation(currentUser, otherUser);

        // Mark messages as read
        messagesService.markConversationAsRead(currentUser, otherUser);

        return ResponseEntity.ok(conversation);
    }

    // REST endpoint to get all user's messages
    @GetMapping("/api/messages/all")
    public ResponseEntity<List<Messages>> getAllMessages(
            @RequestHeader("Authorization") String token) {

        Users currentUser = authService.getCurrentUser(token);
        List<Messages> messages = messagesService.getAllMessages(currentUser);
        return ResponseEntity.ok(messages);
    }

    // REST endpoint to get unread messages
    @GetMapping("/api/messages/unread")
    public ResponseEntity<List<Messages>> getUnreadMessages(
            @RequestHeader("Authorization") String token) {

        Users currentUser = authService.getCurrentUser(token);
        List<Messages> unreadMessages = messagesService.getUnreadMessages(currentUser);
        return ResponseEntity.ok(unreadMessages);
    }

    // REST endpoint to get unread message count
    @GetMapping("/api/messages/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("Authorization") String token) {

        Users currentUser = authService.getCurrentUser(token);
        long count = messagesService.getUnreadCount(currentUser);

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(response);
    }

    // REST endpoint to mark a message as read
    @PutMapping("/api/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long messageId) {

        messagesService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    // REST endpoint to get all conversation partners
    @GetMapping("/api/messages/conversations")
    public ResponseEntity<List<Users>> getConversationPartners(
            @RequestHeader("Authorization") String token) {

        Users currentUser = authService.getCurrentUser(token);
        List<Users> partners = messagesService.getConversationPartners(currentUser);
        return ResponseEntity.ok(partners);
    }

    // REST endpoint to delete a message
    @DeleteMapping("/api/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable Long messageId) {

        Users currentUser = authService.getCurrentUser(token);
        messagesService.deleteMessage(messageId, currentUser);
        return ResponseEntity.ok().build();
    }
}