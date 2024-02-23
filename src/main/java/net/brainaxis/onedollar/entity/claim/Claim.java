package net.brainaxis.onedollar.entity.claim;


import lombok.Data;
import net.brainaxis.onedollar.entity.banner.OfferCategory;
import net.brainaxis.onedollar.entity.customer.User;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
public class Claim implements Serializable {

    @DBRef
    private User user;

    private OfferCategory offerCategory;

    @DBRef
    private String linkTraversed;

    public String getEmail(){
        return user.getEmail();
    }

    public String getFullName(){
        return user.getDisplayName();
    }

}
