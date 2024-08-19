package project.atch.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.atch.domain.user.entity.Character;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {

    // user
    private Long userId;
    private String nickname;
    private List<String> hashTag;
    private Double latitude; // 위도
    private Double longitude; // 경도

    // character
    private String characterImage;
    private List<SlotDetail> slots;

    // item
    private List<ItemDetail> items;

    public UserDetailDto(User user, Character character, Item item1, Item item2, Item item3){
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.hashTag = Arrays.asList(user.getHashTag().split(","));
        this.latitude = user.getLatitude(); // 위도
        this.longitude = user.getLongitude(); // 경도
        this.characterImage = character.getImage();
        this.slots = new ArrayList<>();
        slots.add(new SlotDetail(character.getX1(), character.getY1()));
        slots.add(new SlotDetail(character.getX2(), character.getY2()));
        slots.add(new SlotDetail(character.getX3(), character.getY3()));
        this.items = Arrays.asList(
                new ItemDetail(item1 != null ? item1.getId() : null, item1 != null ? item1.getImage() : null),
                new ItemDetail(item2 != null ? item2.getId() : null, item2 != null ? item2.getImage() : null),
                new ItemDetail(item3 != null ? item3.getId() : null, item3 != null ? item3.getImage() : null)
        );
    }

}
