package net.brainaxis.onedollar.controller;


import net.brainaxis.onedollar.entity.banner.Banner;
import net.brainaxis.onedollar.entity.banner.BannerCategory;
import net.brainaxis.onedollar.entity.banner.BannerPhoto;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static net.brainaxis.onedollar.entity.banner.BannerCategory.VipOffer;

@RestController("banner")
@RequestMapping("/banner")
@CrossOrigin(origins = {"https://react-next-js-with-type-script-admin.vercel.app/", "https://one-dollar-customer-frontend.vercel.app/", "http://localhost:3000"}, allowCredentials = "true", allowedHeaders = "*")
public class BannerController {

    private final Logger logger = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/show/{id}")
    public Banner showBanner(@PathVariable String id) {
        logger.info("Banner:  fetching banner with id = {}", id);

        Banner banner = mongoTemplate.findById(id, Banner.class);

        Query query = new Query()
                .addCriteria(Criteria.where("banner._id").is(id).and("deleted").in(Arrays.asList(false)));

        query.fields().include("id");

        List<String> bannerPhotoList = mongoTemplate.find(query, BannerPhoto.class)
                .stream()
                .map(BannerPhoto::getId)
                .toList();

        banner.setBannerPhotoIdList(bannerPhotoList);

        return banner;
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable String id) {
        logger.info("Banner:  fetching image with id = {}", id);

        BannerPhoto bannerPhoto = mongoTemplate.findById(id, BannerPhoto.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", bannerPhoto.getFileName());

        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bannerPhoto.getPhoto().getData()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }

    @PostMapping("/show/{id}")
    public ResponseEntity<?> updateBanner(@PathVariable String id, @ModelAttribute Banner banner) {
        logger.info("Banner:  updating banner with id = {}", id);

        if (banner.getDeletedPhotoIdList() != null && !banner.getDeletedPhotoIdList().isEmpty()) {
            Query query = new Query()
                    .addCriteria(
                            Criteria
                                    .where("id").in(banner.getDeletedPhotoIdList())
                                    .and("banner._id")
                                    .is(id)
                    );

            Update update = new Update().set("deleted", true);
            mongoTemplate.updateMulti(query, update, BannerPhoto.class);
        }

        mongoTemplate.save(banner);
        saveBannerPhoto(banner);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public Banner deleteBanner(@PathVariable String id, @ModelAttribute Banner banner) {
        logger.info("Banner:  deleting banner with id = {}", id);

        Query query = new Query()
                .addCriteria(
                        Criteria.where("id").is(id)
                );

        Update update = new Update().set("deleted", true);
        mongoTemplate.updateFirst(query, update, Banner.class);

        return mongoTemplate.findById(id, Banner.class);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBanner(@ModelAttribute Banner banner) {
        logger.info("Banner:  creating banner");

        mongoTemplate.save(banner);
        saveBannerPhoto(banner);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<Banner> showBannerList() {
        logger.info("Banner:  fetching all banners");
        return mongoTemplate.findAll(Banner.class);
    }

    @GetMapping("/offerList/{bannerCategory}")
    public List<Banner> showVipOfferDetailedList(@PathVariable BannerCategory bannerCategory) {
        logger.info("Banner:  fetching all vip offer banners with details");

        Query bannerQuery = new Query()
                .addCriteria(
                        Criteria
                                .where("deleted")
                                .is(false)
                                .and("bannerCategory")
                                .is(bannerCategory)
                );

        List<Banner> bannerList = mongoTemplate.find(bannerQuery, Banner.class);

        return bannerList
                .stream()
                .peek(banner -> {
                    Query query = new Query()
                            .addCriteria(
                                    Criteria
                                            .where("banner._id")
                                            .is(banner.getId())
                            );

                    query.fields().include("id");

                    banner.setBannerPhotoIdList(
                            mongoTemplate.find(query, BannerPhoto.class)
                                    .stream()
                                    .map(BannerPhoto::getId)
                                    .toList()
                    );
                })
                .toList();

    }

    @GetMapping("/vipOffer/list")
    public List<Banner> showVipOfferList() {
        logger.info("Banner:  fetching all vip offers");

        Query query = new Query()
                .addCriteria(
                        Criteria.where("bannerCategory").is(VipOffer)
                );

        return mongoTemplate.find(query, Banner.class);
    }

    private void saveBannerPhoto(@ModelAttribute Banner banner) {
        List<MultipartFile> photoList = banner.getPhotoList();

        if (photoList != null) {
            photoList.stream()
                    .filter(Objects::nonNull)
                    .filter(multipartFile -> !multipartFile.isEmpty())
                    .map(multipartFile -> createBannerPhoto(banner, multipartFile))
                    .forEach(bannerPhoto -> mongoTemplate.save(bannerPhoto));
        }
    }

    private BannerPhoto createBannerPhoto(Banner banner, MultipartFile file) {
        try {
            return new BannerPhoto(
                    banner,
                    file.getOriginalFilename(),
                    new Binary(file.getBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
