package project.atch.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class UserDetailDto {

    private Long userId;
    private String nickname;
    private String hashTag;
    private String characterImage;
    private String itemImage;
    private Double itemX;
    private Double itemY;
    private Double latitude; // 위도
    private Double longitude; // 경도

    static public UserDetailDto of(User user, String item){
        return new UserDetailDto(user.getId(), user.getNickname(), user.getHashTag(), user.getCharacter().getImage(),
                item, user.getCharacter().getItemX(), user.getCharacter().getItemY() ,user.getLatitude(), user.getLongitude());
    }
}
