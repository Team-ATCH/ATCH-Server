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
    private Long fromId;
    private String fromNickname;
    private List<String> hashTag;
    private String image;
    private Date createdAt;

    static public MyMessagePreviewDto of(Chat chat, User user){
        return new MyMessagePreviewDto(chat.getRoomId(), chat.getContent(), chat.getFromId(), user.getNickname(),
                Arrays.asList(user.getHashTag().split(",")), user.getCharacter().getImage(), chat.getCreatedAt());
    }

}
