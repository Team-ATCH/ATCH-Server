package project.atch.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ResponseMessageDto {

    private ObjectId id;
    private Long roomId;
    private String content;
    private Long writerId;
    private Date createdDate;

    static public ResponseMessageDto of(Chat chat) {
        return new ResponseMessageDto(chat.getId(), chat.getRoomId(),
                chat.getContent(), chat.getFromUserId(), chat.getCreatedDate());
    }
}
