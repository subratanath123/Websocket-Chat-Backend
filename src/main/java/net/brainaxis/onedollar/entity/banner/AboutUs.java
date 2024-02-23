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
public class AboutUs implements Serializable {

    @Id
    private String id;
    private String aboutUsDetails;

    @Transient
    private List<MultipartFile> photoList;

    @Transient
    private List<String> aboutUsPhotoIdList;

    @Transient
    private List<String> deletedPhotoIdList;

}

