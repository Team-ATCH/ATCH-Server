package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RequestBlockDto {

    private final long userId;

    public RequestBlockDto(@JsonProperty("userId") long userId){
        this.userId = userId;
    }
}
