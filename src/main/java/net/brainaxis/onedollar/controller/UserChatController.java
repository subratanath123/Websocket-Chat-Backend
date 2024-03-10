package net.brainaxis.onedollar.controller;

import lombok.extern.slf4j.Slf4j;
import net.brainaxis.onedollar.dao.ChatDao;
import net.brainaxis.onedollar.dto.ChatMessage;
import net.brainaxis.onedollar.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RequestMapping("/v1/api")
public class UserChatController {

    @Autowired
    private ChatDao chatDao;

    @RequestMapping("/get-recent-messages/{userEmail}")
    public List<ChatMessage> sendToOtherUser(@PathVariable String userEmail) {
        log.info("get recent messages with {}", userEmail);

        String chatHistoryId = chatDao.getChatHistoryId(userEmail, AuthUtils.getEmail());

        List<ChatMessage> chatMessageList = chatDao.getChatHistory(chatHistoryId).getMessages();

        return chatMessageList != null
                ? chatMessageList
                : new ArrayList<>();
    }

}
