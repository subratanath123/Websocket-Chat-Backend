package net.ai.chatbot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ai.chatbot.enums.MessageType;
import net.ai.chatbot.dto.ChatMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");

        if (userEmail != null) {
            log.info("user disconnected: {}", userEmail);

            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .senderEmail(userEmail)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

}