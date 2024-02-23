package net.brainaxis.onedollar.entity.banner;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Document
@Data
public class Banner implements Serializable {

    @Id
    private String id;
    private String link;
    private String order;
    private String buttonName;
    private boolean deleted;

    @Transient
    private List<MultipartFile> photoList;

    @Transient
    private List<String> bannerPhotoIdList;

    @Transient
    private List<String> deletedPhotoIdList;

    private String validityFrom;
    private String validityTo;
    private String bannerDetails;
    @Field
    private BannerCategory bannerCategory;

    @Field
    private OfferCategory offerCategory;

}
