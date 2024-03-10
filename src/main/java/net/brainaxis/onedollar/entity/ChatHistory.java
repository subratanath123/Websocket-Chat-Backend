package net.brainaxis.onedollar.entity;

import lombok.*;
import net.brainaxis.onedollar.dto.ChatMessage;
import net.brainaxis.onedollar.dto.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class ChatHistory {

    @Id
    private String id;

    private List<User> users;
    private List<ChatMessage> messages;

}
