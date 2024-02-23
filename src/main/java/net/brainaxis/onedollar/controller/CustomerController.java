package net.brainaxis.onedollar.controller;

import net.brainaxis.onedollar.entity.banner.AboutUsPhoto;
import net.brainaxis.onedollar.entity.claim.Claim;
import net.brainaxis.onedollar.entity.customer.User;
import net.brainaxis.onedollar.entity.customer.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController("customer")
@RequestMapping("customer")
@CrossOrigin(origins = {"https://react-next-js-with-type-script-admin.vercel.app/", "https://one-dollar-customer-frontend.vercel.app/", "http://localhost:3000" }, allowCredentials = "true", allowedHeaders = "*")
public class CustomerController {

    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/list")
    public List<UserDto> showCustomerList() {
        logger.info("Customer:  fetching all Customer");

        List<User> userList = mongoTemplate.findAll(User.class);

        return userList.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getEmail(),
                        user.getDisplayName(),
                        user.getCurrentSubscriptionType() != null,
                        user.getCurrentSubscriptionType() != null ? user.getCurrentSubscriptionType().getCreated() : null,
                        user.getCurrentSubscriptionType() != null ? user.getCurrentSubscriptionType().getEndDate() : null,
                        user.getCurrentSubscriptionType() != null ? user.getCurrentSubscriptionType().getSubscriptionType() : null,
                        user.getUserSubscriptionHistory() != null ? user.getUserSubscriptionHistory().size() : 0

                )).collect(Collectors.toList());

    }

    @GetMapping("/claim/{customerId}")
    public List<Claim> showClaimList(@PathVariable String customerId) {
        logger.info("Customer:  fetching all claims for a customer");

        Query query = new Query()
                .addCriteria(Criteria.where("user._id").is(customerId));

        return mongoTemplate.find(query, Claim.class);

    }
}
