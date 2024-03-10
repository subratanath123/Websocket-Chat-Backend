package net.brainaxis.onedollar.dto;

import lombok.*;
import net.brainaxis.onedollar.enums.MessageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String content;
    private String senderEmail;
    private String attachmentLink;
    private Date created;

    @Transient
    private String token;

    @Transient
    private MessageType type;
}