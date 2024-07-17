package project.atch.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.atch.domain.user.entity.Character;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ResponseItemDto {
    private String characterImage; // 현재 캐릭터의 이미지
    private List<SlotDetail> slots; // 아이템의 위치 세 개
    private List<ItemDetail> items; // 사용자가 가지고 있는 아이템 이미지 리스트

    public ResponseItemDto(Character character, List<ItemDetail> items){
        this.characterImage = character.getImage();
        this.slots = new ArrayList<>();
        slots.add(new SlotDetail(character.getX1(), character.getY1()));
        slots.add(new SlotDetail(character.getX2(), character.getY2()));
        slots.add(new SlotDetail(character.getX3(), character.getY3()));
        this.items = items;
    }
}
