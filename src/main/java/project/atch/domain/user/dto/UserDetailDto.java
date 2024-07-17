package project.atch.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.atch.domain.user.entity.Character;
import project.atch.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {

    // user
    private Long userId;
    private String nickname;
    private String hashTag;
    private Double latitude; // 위도
    private Double longitude; // 경도

    // character
    private String characterImage;
    private List<SlotDetail> slots;

    // item
    private List<ItemDetail> items;

    public UserDetailDto(User user, Character character, List<ItemDetail> items){
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.hashTag = user.getHashTag();
        this.latitude = user.getLatitude(); // 위도
        this.longitude = user.getLongitude(); // 경도
        this.characterImage = character.getImage();
        this.slots = new ArrayList<>();
        slots.add(new SlotDetail(character.getX1(), character.getY1()));
        slots.add(new SlotDetail(character.getX2(), character.getY2()));
        slots.add(new SlotDetail(character.getX3(), character.getY3()));
        this.items = items;
    }


}
