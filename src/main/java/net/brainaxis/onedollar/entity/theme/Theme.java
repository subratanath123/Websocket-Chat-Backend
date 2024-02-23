package net.brainaxis.onedollar.entity.theme;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Document
@Data
public class Theme implements Serializable {

    @Id
    private String id;

    @Transient
    private List<MultipartFile> photoList;

    @Transient
    private List<String> aboutUsPhotoIdList;

    @Transient
    private List<String> deletedPhotoIdList;

}

