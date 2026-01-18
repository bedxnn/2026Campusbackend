package campusbackend.websocket;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cors.allowed.origin:http://localhost:5173}")
    private String allowedOrigin;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages bound for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // Prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws endpoint for WebSocket connections
        // SECURITY: Only allow specific origins to prevent CSRF attacks
        // Configure via 'cors.allowed.origin' property (defaults to localhost:5173)
        // For production, set this to your actual frontend domain
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigin)
                .withSockJS(); // Enable SockJS fallback options

        // Also register without SockJS for native WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigin);
    }
}