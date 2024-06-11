package project.atch.domain.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDto {

    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime sendAt;

}
