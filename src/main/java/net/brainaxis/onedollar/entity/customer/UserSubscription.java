package net.brainaxis.onedollar.entity.customer;

import lombok.Data;
import net.brainaxis.onedollar.entity.enums.SubscriptionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@Document
public class UserSubscription implements Serializable {

    @Id
    private String id;

    @DBRef
    private User user;

    private Date created;
    private Date endDate;
    private SubscriptionType subscriptionType;
    private boolean recurring;
    private double price;

}
