package net.brainaxis.onedollar.dao;

import net.brainaxis.onedollar.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String fetchUserId(String email) {
        Query query = new Query()
                .addCriteria(Criteria.where("email").is(email));

        query.fields().include("id");

        return Optional.ofNullable(mongoTemplate.findOne(query, User.class))
                .map(User::getId)
                .orElse(null);
    }

    public void saveUserIfNotExists(User user) {
        if (fetchUserId(user.getEmail()) == null) {
            mongoTemplate.save(user);
        }
    }

    public List<User> loadUsers() {
        return mongoTemplate.findAll(User.class);
    }

}