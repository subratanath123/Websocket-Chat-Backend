package net.brainaxis.onedollar.entity.review;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
public class ReviewPhoto implements Serializable {

    @Id
    private String id;

    @DBRef
    private Review review;

    private String fileName;
    private Binary photo;
    private boolean deleted;

    public ReviewPhoto() {
    }

    public ReviewPhoto(Review review, String fileName, Binary photo) {
        this.review = review;
        this.fileName = fileName;
        this.photo = photo;
    }
}
