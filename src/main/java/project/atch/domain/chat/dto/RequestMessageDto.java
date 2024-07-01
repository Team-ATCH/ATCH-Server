package project.atch.domain.chat.dto;

import lombok.Getter;

@Getter
public class RequestMessageDto {
    private final String content;
    private final Long fromId;

    public RequestMessageDto(String content, Long fromId) {
        this.content = content;
        this.fromId = fromId;
    }
}
