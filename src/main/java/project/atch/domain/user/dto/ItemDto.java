package project.atch.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import project.atch.domain.user.entity.Character;

import java.util.List;

public class ItemDto {
    public record ItemReq(
            @JsonProperty("itemId1") Long itemId1,
            @JsonProperty("itemId2") Long itemId2,
            @JsonProperty("itemId3") Long itemId3
    ) {}

    public record ItemRes(
            String characterImage,
            List<SlotDetail> slots,
            List<ItemDetail> items) {
        public ItemRes(Character character, List<ItemDetail> items) {
            this(character.getImage(),
                    List.of(new SlotDetail(character.getX1(), character.getY1()),
                            new SlotDetail(character.getX2(), character.getY2()),
                            new SlotDetail(character.getX3(), character.getY3())),
                    items);
        }
    }
}
