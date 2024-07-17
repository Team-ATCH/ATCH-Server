package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RequestItemDto {
    private final Long itemId1;
    private final Long itemId2;
    private final Long itemId3;

    public RequestItemDto(@JsonProperty("itemId1") long itemId1, @JsonProperty("itemId2") long itemId2, @JsonProperty("itemId3") long itemId3){
        this.itemId1 = itemId1;
        this.itemId2 = itemId2;
        this.itemId3 = itemId3;
    }
}
