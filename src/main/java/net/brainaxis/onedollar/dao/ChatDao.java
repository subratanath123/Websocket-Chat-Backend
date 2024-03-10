package net.brainaxis.onedollar.dao;

import com.mongodb.client.result.UpdateResult;
import net.brainaxis.onedollar.dto.ChatMessage;
import net.brainaxis.onedollar.dto.User;
import net.brainaxis.onedollar.entity.ChatHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static java.util.Arrays.asList;

@Repository
public class ChatDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserDao userDao;

    public String getChatHistoryId(String userOneEmail, String userTwoEmail) {
        return getChatHistory(userOneEmail, userTwoEmail).getId();
    }

    public ChatHistory getChatHistory(String userOneEmail, String userTwoEmail) {
        String userOneId = userDao.fetchUserId(userOneEmail);
        String userTwoId = userDao.fetchUserId(userTwoEmail);

        Query query = new Query()
                .addCriteria(
                        new Criteria()
                                .andOperator(
                                        Criteria.where("users._id").in(userOneId),  // userOneId exists anywhere in users._id
                                        Criteria.where("users._id").in(userTwoId)   // userTwoId exists anywhere in users._id
                                )
                );

        query.fields().include("id");

        return Optional.ofNullable(mongoTemplate.findOne(query, ChatHistory.class))
                .orElseGet(() -> {
                    ChatHistory chatHistory = ChatHistory.builder()
                            .users(asList(User.builder().id(userOneId).build(), User.builder().id(userTwoId).build()))
                            .build();

                    return mongoTemplate.save(chatHistory);
                });
    }

    public ChatHistory getChatHistory(String id) {
        return mongoTemplate.findById(id, ChatHistory.class);
    }

    public UpdateResult saveChatHistory(String chatHistoryId, ChatMessage chatMessage) {
        Query query = new Query(
                Criteria.where("id")
                        .is(chatHistoryId)
        );

        Update update = new Update().addToSet("messages", chatMessage);

        return mongoTemplate.updateFirst(query, update, ChatHistory.class);
    }
}
