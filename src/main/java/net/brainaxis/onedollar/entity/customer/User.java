package net.brainaxis.onedollar.entity.customer;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Document
@Data
public class User implements Serializable {

    @Id
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private String gender;
    private Instant created;

    @DBRef
    private UserSubscription currentSubscriptionType;

    @DBRef
    private List<UserSubscription> userSubscriptionHistory;

}