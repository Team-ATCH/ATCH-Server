package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestHashTagDto {
    private final List<String> hashTag;

    public RequestHashTagDto(@JsonProperty("hashTag") List<String> hashTag){
        this.hashTag = hashTag;
    }
}
