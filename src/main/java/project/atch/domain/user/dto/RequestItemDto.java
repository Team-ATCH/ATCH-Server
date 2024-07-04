package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RequestItemDto {
    private final long itemId;

    public RequestItemDto(@JsonProperty("itemId") long itemId){
        this.itemId = itemId;
    }
}
