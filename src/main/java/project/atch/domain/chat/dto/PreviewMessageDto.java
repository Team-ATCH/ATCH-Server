package project.atch.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.chat.entity.Chat;

import java.util.Date;


@Getter
@AllArgsConstructor
public class PreviewMessageDto {
    private Long roomId;
    private String content;
    private Long fromId;
    private String fromNickname;
    private Date createdAt;
    private Boolean read;

    static public PreviewMessageDto of(Chat chat, String nickname) {
        return new PreviewMessageDto(chat.getRoomId(), chat.getContent(), chat.getFromId(),
                nickname, chat.getCreatedAt(), chat.isRead());
    }

}
