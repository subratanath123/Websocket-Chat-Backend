
package net.brainaxis.onedollar.controller;


import net.brainaxis.onedollar.entity.theme.Theme;
import net.brainaxis.onedollar.entity.theme.ThemePhoto;
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

@RestController("theme")
@RequestMapping("/theme")
@CrossOrigin(origins = {"https://react-next-js-with-type-script-admin.vercel.app/", "http://localhost:3000" }, allowCredentials = "true", allowedHeaders = "*")
public class ThemeController {

    private final Logger logger = LoggerFactory.getLogger(ThemeController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping
    public Theme showTheme() {
        Theme theme = Optional.of(mongoTemplate.findAll(Theme.class))
                .orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .orElse(null);

        if (theme != null) {
            logger.info("Theme:  showing Theme with id = {}", theme.getId());

            Query query = new Query()
                    .addCriteria(
                            Criteria.where("theme._id")
                                    .is(theme.getId())
                                    .and("deleted")
                                    .in(Arrays.asList(null, false))
                    );

            query.fields().include("id");

            List<String> photoIdList = mongoTemplate.find(query, ThemePhoto.class)
                    .stream()
                    .map(ThemePhoto::getId)
                    .toList();

            theme.setAboutUsPhotoIdList(photoIdList);
        }


        return theme;
    }

    @PostMapping
    public ResponseEntity<?> updateTheme(@ModelAttribute Theme theme) {
        logger.info("AboutUs:  updating AboutUs with id = {}", theme.getId());

        if (theme.getDeletedPhotoIdList() != null && !theme.getDeletedPhotoIdList().isEmpty()) {
            Query query = new Query()
                    .addCriteria(
                            Criteria
                                    .where("id").in(theme.getDeletedPhotoIdList())
                                    .and("theme._id")
                                    .is(theme.getId())
                    );

            Update update = new Update().set("deleted", true);
            mongoTemplate.updateMulti(query, update, ThemePhoto.class);
        }

        mongoTemplate.save(theme);
        saveThemePhoto(theme);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String id) {
        logger.info("theme:  fetching theme image with id = {}", id);

        ThemePhoto themePhoto = mongoTemplate.findById(id, ThemePhoto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", themePhoto.getFileName());

        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(themePhoto.getPhoto().getData()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }

    private void saveThemePhoto(@ModelAttribute Theme theme) {
        List<MultipartFile> photoList = theme.getPhotoList();

        if (photoList != null) {
            photoList.stream()
                    .filter(Objects::nonNull)
                    .filter(multipartFile -> !multipartFile.isEmpty())
                    .map(multipartFile -> createThemePhoto(theme, multipartFile))
                    .forEach(themePhoto -> mongoTemplate.save(themePhoto));
        }
    }

    private ThemePhoto createThemePhoto(Theme theme, MultipartFile file) {
        try {
            return new ThemePhoto(
                    theme,
                    file.getOriginalFilename(),
                    new Binary(file.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
