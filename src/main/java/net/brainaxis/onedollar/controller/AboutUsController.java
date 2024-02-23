
package net.brainaxis.onedollar.controller;


import net.brainaxis.onedollar.entity.banner.AboutUs;
import net.brainaxis.onedollar.entity.banner.AboutUsPhoto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@RestController("aboutus")
@RequestMapping("/aboutus")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", allowedHeaders = "*")
public class AboutUsController {

    private final Logger logger = LoggerFactory.getLogger(AboutUsController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping
    public AboutUs showAboutUs() {
        AboutUs aboutUs = Optional.of(mongoTemplate.findAll(AboutUs.class))
                .orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .orElse(null);

        if (aboutUs != null) {
            logger.info("AboutUs:  showing AboutUs with id = {}", aboutUs.getId());

            Query query = new Query()
                    .addCriteria(
                            Criteria.where("aboutUs._id")
                                    .is(aboutUs.getId())
                                    .and("deleted")
                                    .in(Arrays.asList(null, false))
                    );

            query.fields().include("id");

            List<String> photoIdList = mongoTemplate.find(query, AboutUsPhoto.class)
                    .stream()
                    .map(AboutUsPhoto::getId)
                    .toList();

            aboutUs.setAboutUsPhotoIdList(photoIdList);
        }


        return aboutUs;
    }

    @PostMapping
    public ResponseEntity<?> updateAboutUs(@ModelAttribute AboutUs aboutUs) {
        logger.info("AboutUs:  updating AboutUs with id = {}", aboutUs.getId());

        if (aboutUs.getDeletedPhotoIdList() != null && !aboutUs.getDeletedPhotoIdList().isEmpty()) {
            Query query = new Query()
                    .addCriteria(
                            Criteria
                                    .where("id").in(aboutUs.getDeletedPhotoIdList())
                                    .and("aboutUs._id")
                                    .is(aboutUs.getId())
                    );

            Update update = new Update().set("deleted", true);
            mongoTemplate.updateMulti(query, update, AboutUsPhoto.class);
        }

        mongoTemplate.save(aboutUs);
        saveReviewPhoto(aboutUs);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String id) {
        logger.info("AboutUS:  fetching image with id = {}", id);

        AboutUsPhoto aboutUsPhoto = mongoTemplate.findById(id, AboutUsPhoto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", aboutUsPhoto.getFileName());

        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(aboutUsPhoto.getPhoto().getData()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }

    private void saveReviewPhoto(@ModelAttribute AboutUs aboutUs) {
        List<MultipartFile> photoList = aboutUs.getPhotoList();

        if (photoList != null) {
            photoList.stream()
                    .filter(Objects::nonNull)
                    .filter(multipartFile -> !multipartFile.isEmpty())
                    .map(multipartFile -> createReviewPhoto(aboutUs, multipartFile))
                    .forEach(aboutUsPhoto -> mongoTemplate.save(aboutUsPhoto));
        }
    }

    private AboutUsPhoto createReviewPhoto(AboutUs aboutUs, MultipartFile file) {
        try {
            return new AboutUsPhoto(
                    aboutUs,
                    file.getOriginalFilename(),
                    new Binary(file.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
