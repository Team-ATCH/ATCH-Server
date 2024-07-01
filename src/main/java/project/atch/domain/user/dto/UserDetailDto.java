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
    // TODO 캐릭터 추가
    private Double latitude; // 위도
    private Double longitude; // 경도

    static public UserDetailDto of(User user){
        return new UserDetailDto(user.getId(), user.getNickname(), user.getHashTag(), user.getLatitude(), user.getLongitude());
    }
}
