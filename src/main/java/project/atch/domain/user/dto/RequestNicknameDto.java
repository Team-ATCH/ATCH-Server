package project.atch.domain.user.dto;

import lombok.Getter;

@Getter
public class RequestNicknameDto {
    private final String nickname;
    public RequestNicknameDto(String nickname){
        this.nickname = nickname;
    }
}
