package project.atch.domain.chat.dto;

import lombok.Getter;

@Getter
public class RequestMessageDto {
    private String content;
    private Long fromId;

    public RequestMessageDto(String content, Long fromId) {
        this.content = content;
        this.fromId = fromId;
    }
}
