package project.atch.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.chat.entity.Chat;

@Getter
@AllArgsConstructor
public class ResponseMessageDto {

    private String content;
    private Long fromId;
    private String fromNickname;

    static public ResponseMessageDto of(Chat chat, String nickname) {
        return new ResponseMessageDto(chat.getContent(), chat.getFromId(), nickname);
    }
}
