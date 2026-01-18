package campusbackend.websocket;


import campusbackend.auth.Users;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MessagesService {

    private final MessagesRepository messagesRepository;

    public MessagesService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    @Transactional
    public Messages sendMessage(Users sender, Users receiver, String content) {
        Messages message = new Messages();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        return messagesRepository.save(message);
    }

    public List<Messages> getConversation(Users user1, Users user2) {
        return messagesRepository.findConversation(user1, user2);
    }

    public List<Messages> getAllMessages(Users user) {
        List<Messages> sentMessages = messagesRepository.findBySender(user);
        List<Messages> receivedMessages = messagesRepository.findByReceiver(user);

        return Stream.concat(sentMessages.stream(), receivedMessages.stream())
                .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()))
                .toList();
    }

    public List<Messages> getUnreadMessages(Users user) {
        return messagesRepository.findByReceiverAndIsReadFalse(user);
    }

    public long getUnreadCount(Users user) {
        return messagesRepository.countByReceiverAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        Messages message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messagesRepository.save(message);
    }

    @Transactional
    public void markConversationAsRead(Users currentUser, Users otherUser) {
        List<Messages> unreadMessages = messagesRepository.findBySenderAndReceiver(otherUser, currentUser);
        for (Messages message : unreadMessages) {
            if (!message.isRead()) {
                message.setRead(true);
                messagesRepository.save(message);
            }
        }
    }

    public List<Users> getConversationPartners(Users user) {
        return messagesRepository.findConversationPartners(user);
    }

    @Transactional
    public void deleteMessage(Long messageId, Users user) {
        Messages message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Only allow deletion if user is sender or receiver
        if (message.getSender().equals(user) || message.getReceiver().equals(user)) {
            messagesRepository.delete(message);
        } else {
            throw new RuntimeException("Unauthorized to delete this message");
        }
    }
}