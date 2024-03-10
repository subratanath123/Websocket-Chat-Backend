package net.brainaxis.onedollar.controller;

import lombok.extern.slf4j.Slf4j;
import net.brainaxis.onedollar.dao.ChatDao;
import net.brainaxis.onedollar.dto.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
@Slf4j
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatDao chatDao;

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setCreated(new Date());

        return chatMessage;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setCreated(new Date());

        return chatMessage;
    }

    @MessageMapping("/user-message-{userEmail}")
    public void sendToOtherUser(@Payload ChatMessage chatMessage,
                                @DestinationVariable String userEmail,
                                @Header("simpSessionId") String sessionId) {

        chatMessage.setCreated(new Date());

        log.info("sending message: source: {}", sessionId);

        String chatHistoryId = chatDao.getChatHistoryId(userEmail, chatMessage.getSenderEmail());
        chatDao.saveChatHistory(chatHistoryId, chatMessage);

        simpMessagingTemplate.convertAndSend("/queue/reply-" + userEmail, chatMessage);
        simpMessagingTemplate.convertAndSend("/queue/reply-" + chatMessage.getSenderEmail(), chatMessage);
    }

}