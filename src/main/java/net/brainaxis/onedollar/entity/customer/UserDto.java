package net.brainaxis.onedollar.entity.customer;

import lombok.Data;
import net.brainaxis.onedollar.entity.enums.SubscriptionType;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserDto implements Serializable {
    private String id;
    private String email;
    private String fullName;
    private boolean hasSubscription;
    private Date subscriptionCreated;
    private Date subscriptionEnded;
    private SubscriptionType subscriptionType;
    private double totalSubscription;

    public UserDto() {
    }

    public UserDto(String id, String email, String fullName, boolean hasSubscription,
                   Date subscriptionCreated, Date subscriptionEnded, SubscriptionType subscriptionType, double totalSubscription) {
        this();
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.hasSubscription = hasSubscription;
        this.subscriptionCreated = subscriptionCreated;
        this.subscriptionEnded = subscriptionEnded;
        this.subscriptionType = subscriptionType;
        this.totalSubscription = totalSubscription;
    }
}
