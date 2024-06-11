package project.atch.domain.chat;

import lombok.Getter;

@Getter
public class RequestMessageDto {
    private String content;
    private Long fromUserId;

    public RequestMessageDto(String content, Long fromUserId) {
        this.content = content;
        this.fromUserId = fromUserId;
    }
}
