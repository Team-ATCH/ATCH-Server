package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RequestCharacterDto {

    private final long characterId;

    public RequestCharacterDto(@JsonProperty("characterId") long characterId){
        this.characterId = characterId;
    }


}