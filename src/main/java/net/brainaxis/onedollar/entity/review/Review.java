package net.brainaxis.onedollar.entity.review;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Document
@Data
public class Review implements Serializable {

    @Id
    private String id;
    private String clientName;
    private String clientDetails;
    private String clientReview;

    private int stars;
    private boolean deleted;

    @Transient
    private List<MultipartFile> clientPhotoList;

    @Transient
    private List<String> clientPhotoIdList;

    @Transient
    private List<String> deletedPhotoIdList;
}

