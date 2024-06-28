package project.atch.domain.chat.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat") // 실제 몽고 DB 컬렉션 이름
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat{
    @Id
    private ObjectId id;

    private Long roomId;

    private String content;

    private Long fromId;


    public Chat(Long roomId, String content, Long fromId) {
        this.roomId = roomId;
        this.content = content;
        this.fromId = fromId;
    }
}
