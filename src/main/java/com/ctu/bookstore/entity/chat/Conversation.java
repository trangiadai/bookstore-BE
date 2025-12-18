package com.ctu.bookstore.entity.chat;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Document
@Getter //recommend using @Getter and @Setter cho entity(google for more). One reason is it easy to manage
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @MongoId
    String id;
    String type;
    @Indexed(unique = true)
    String participantsHash;
    List<ParticipantInfo> participants;
    Instant createDate;
    Instant modifiedDate ;
}
