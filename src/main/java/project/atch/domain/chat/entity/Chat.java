package project.atch.domain.chat.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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

    private Date createdAt;

    private boolean read;

    public Chat(Long roomId, String content, Long fromId, Date date, boolean read) {
        this.roomId = roomId;
        this.content = content;
        this.fromId = fromId;
        this.createdAt = date;
        this.read = read;
    }
}
