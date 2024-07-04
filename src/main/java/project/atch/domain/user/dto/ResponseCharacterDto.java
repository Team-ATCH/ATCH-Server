package project.atch.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.user.entity.Character;

@Getter
@AllArgsConstructor
public class ResponseCharacterDto {
    private Long characterId;
    private String image;

    static public ResponseCharacterDto of(Character character){
        return new ResponseCharacterDto(character.getId(), character.getImage());
    }

}
