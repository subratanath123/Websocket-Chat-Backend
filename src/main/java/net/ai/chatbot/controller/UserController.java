package net.ai.chatbot.controller;

import lombok.extern.slf4j.Slf4j;
import net.ai.chatbot.dao.UserDao;
import net.ai.chatbot.dto.User;
import net.ai.chatbot.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RequestMapping("/v1/api")
public class UserController {

    @Autowired
    private UserDao userDao;

    //TODO: Have to remove duplicacy using redis or something
    @GetMapping("/user/authenticated")
    public String authenticated() {
        log.info("Authenticated user: " + AuthUtils.getEmail());

        userDao.saveUserIfNotExists(AuthUtils.getUser());

        return "ok";
    }

    @GetMapping("/supportUserList")
    public List<User> getSupportUserList() {
        List<User> users = userDao.loadUsers();
        users.add(new User("chatbot-id", "chatbot", "chatbot", "chatbot", ""));

        return users;
    }

    private User getUser(String userName, String email, String designation, String picture) {
        return User
                .builder()
                .userName(userName)
                .email(email)
                .designation(designation)
                .picture(picture)
                .build();
    }

}
