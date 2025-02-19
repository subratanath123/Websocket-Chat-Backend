package net.ai.chatbot.controller;

import lombok.extern.slf4j.Slf4j;
import net.ai.chatbot.dao.ChatDao;
import net.ai.chatbot.dto.ChatMessage;
import net.ai.chatbot.service.OpenAiService;
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

    @Autowired
    private OpenAiService openAiService;

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

        //Sending self message to self chatbox
        simpMessagingTemplate.convertAndSend("/queue/reply-" + chatMessage.getSenderEmail(), chatMessage);

        //Sending other users/chabots message to self chatbox
        if (userEmail.equals("chatbot")) {

            simpMessagingTemplate.convertAndSend("/queue/reply-" + chatMessage.getSenderEmail(), ChatMessage
                    .builder()
                    .content(openAiService.chat(chatMessage.getContent()).getChoices().get(0).getMessage().getContent())
                    .senderEmail(userEmail)
                    .created(new Date())
                    .build()
            );

        } else {
            simpMessagingTemplate.convertAndSend("/queue/reply-" + userEmail, chatMessage);
        }
    }

}