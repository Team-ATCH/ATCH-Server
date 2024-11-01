package project.atch.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.chat.entity.Chat;

import java.util.Date;


@Getter
@AllArgsConstructor
public class OtherMessagePreviewDto {
    private Long roomId;
    private String content;
    private Long fromId;
    private String fromNickname;
    private Date createdAt;
    private Boolean read;

    static public OtherMessagePreviewDto of(Chat chat, String nickname) {
        return new OtherMessagePreviewDto(chat.getRoomId(), chat.getContent(), chat.getFromId(),
                nickname, chat.getCreatedAt(), chat.isRead());
    }

}
