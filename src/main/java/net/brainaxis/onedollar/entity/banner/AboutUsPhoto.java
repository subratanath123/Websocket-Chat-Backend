package net.brainaxis.onedollar.entity.banner;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
public class AboutUsPhoto implements Serializable {

    @Id
    private String id;

    @DBRef
    private AboutUs aboutUs;

    private String fileName;
    private Binary photo;
    private boolean deleted;

    public AboutUsPhoto() {
    }

    public AboutUsPhoto(AboutUs aboutUs, String fileName, Binary photo) {
        this.aboutUs = aboutUs;
        this.fileName = fileName;
        this.photo = photo;
    }
}
