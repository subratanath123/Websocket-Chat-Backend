
package net.brainaxis.onedollar.controller;


import net.brainaxis.onedollar.entity.banner.BannerPhoto;
import net.brainaxis.onedollar.entity.review.Review;
import net.brainaxis.onedollar.entity.review.ReviewPhoto;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@RestController("review")
@RequestMapping("/review")
@CrossOrigin(origins = {"https://react-next-js-with-type-script-admin.vercel.app/", "http://localhost:3000" }, allowCredentials = "true", allowedHeaders = "*")
public class ReviewController {

    private final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/show/{id}")
    public Review showReview(@PathVariable String id) {
        logger.info("Review:  fetching Review with id = {}", id);

        Review review = mongoTemplate.findById(id, Review.class);

        Query query = new Query()
                .addCriteria(
                        Criteria.where("review._id")
                                .is(id)
                                .and("deleted")
                                .in(Arrays.asList(null, false))
                );

        query.fields().include("id");

        List<String> clientPhotoIdList = mongoTemplate.find(query, ReviewPhoto.class)
                .stream()
                .map(ReviewPhoto::getId)
                .toList();

        review.setClientPhotoIdList(clientPhotoIdList);

        return review;
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String id) {
        logger.info("Review:  fetching image with id = {}", id);

        ReviewPhoto reviewPhoto = mongoTemplate.findById(id, ReviewPhoto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", reviewPhoto.getFileName());

        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(reviewPhoto.getPhoto().getData()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }

    @PostMapping("/show/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id, @ModelAttribute Review review) {
        logger.info("Review:  updating Review with id = {}", id);

        if (review.getDeletedPhotoIdList() != null && !review.getDeletedPhotoIdList().isEmpty()) {
            Query query = new Query()
                    .addCriteria(
                            Criteria
                                    .where("id").in(review.getDeletedPhotoIdList())
                                    .and("banner._id")
                                    .is(id)
                    );

            Update update = new Update().set("deleted", true);
            mongoTemplate.updateMulti(query, update, BannerPhoto.class);
        }

        mongoTemplate.save(review);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public Review deleteReview(@PathVariable String id, @ModelAttribute Review review) {
        logger.info("Review:  deleting review with id = {}", id);

        Query query = new Query()
                .addCriteria(
                        Criteria.where("id").is(id)
                );

        Update update = new Update().set("deleted", true);
        mongoTemplate.updateFirst(query, update, Review.class);

        return mongoTemplate.findById(id, Review.class);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createReview(@ModelAttribute Review review) {
        logger.info("Review: creating Review");

        mongoTemplate.save(review);
        saveReviewPhoto(review);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<Review> showReviewList() {
        logger.info("Review:  fetching all reviews");

        return mongoTemplate.findAll(Review.class);
    }

    private void saveReviewPhoto(@ModelAttribute Review review) {
        List<MultipartFile> photoList = review.getClientPhotoList();

        if (photoList != null) {
            photoList.stream()
                    .filter(Objects::nonNull)
                    .filter(multipartFile -> !multipartFile.isEmpty())
                    .map(multipartFile -> createReviewPhoto(review, multipartFile))
                    .forEach(bannerPhoto -> mongoTemplate.save(bannerPhoto));
        }
    }

    private ReviewPhoto createReviewPhoto(Review review, MultipartFile file) {
        try {
            return new ReviewPhoto(
                    review,
                    file.getOriginalFilename(),
                    new Binary(file.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
