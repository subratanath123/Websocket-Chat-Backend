package net.brainaxis.onedollar.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BannerDao {


    @Autowired
    private MongoTemplate mongoTemplate;


}
