package net.brainaxis.onedollar.entity.banner;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
public class BannerPhoto implements Serializable {

    @Id
    private String id;

    @DBRef
    private Banner banner;

    private String fileName;
    private Binary photo;
    private boolean deleted;

    public BannerPhoto() {
    }

    public BannerPhoto(Banner banner, String fileName, Binary photo) {
        this.banner = banner;
        this.fileName = fileName;
        this.photo = photo;
    }
}
