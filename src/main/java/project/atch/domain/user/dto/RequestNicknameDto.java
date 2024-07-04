package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RequestNicknameDto {
    private final String nickname;
    public RequestNicknameDto(@JsonProperty("nickname") String nickname){
        this.nickname = nickname;
    }
}
