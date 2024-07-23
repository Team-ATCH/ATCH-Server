package project.atch.domain.chat.dto;


import jakarta.validation.constraints.NotNull;
import project.atch.domain.chat.entity.Chat;

import java.util.Date;


public class MessageDto {

    public record Req(
            @NotNull(message = "메세지는 공란일 수 없습니다.") String content) {
    }

    public record Res(
            String content,
            Long fromId,
            String fromNickname,
            Date createdAt,
            Boolean read) {
        public static Res of(Chat chat, String nickname) {
            return new Res(chat.getContent(), chat.getFromId(), nickname, chat.getCreatedAt(), chat.isRead());
        }
    }
}