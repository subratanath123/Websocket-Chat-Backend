package net.brainaxis.onedollar.entity.theme;

import lombok.Data;
import net.brainaxis.onedollar.entity.banner.AboutUs;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
public class ThemePhoto implements Serializable {

    @Id
    private String id;

    @DBRef
    private Theme theme;

    private String fileName;
    private Binary photo;
    private boolean deleted;

    public ThemePhoto() {
    }

    public ThemePhoto(Theme theme, String fileName, Binary photo) {
        this.theme = theme;
        this.fileName = fileName;
        this.photo = photo;
    }
}
