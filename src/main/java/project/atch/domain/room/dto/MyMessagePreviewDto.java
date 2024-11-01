package project.atch.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.chat.entity.Chat;
import project.atch.domain.user.entity.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class MyMessagePreviewDto {
    private Long roomId;
    private String content;
    private Long opponentId;
    private String opponentNickname;
    private List<String> hashTag;
    private String image;
    private Date createdAt;

    static public MyMessagePreviewDto of(Chat chat, User opponent){
        return new MyMessagePreviewDto(chat.getRoomId(), chat.getContent(), opponent.getId(), opponent.getNickname(),
                Arrays.asList(opponent.getHashTag().split(",")), opponent.getCharacter().getImage(), chat.getCreatedAt());
    }

}
