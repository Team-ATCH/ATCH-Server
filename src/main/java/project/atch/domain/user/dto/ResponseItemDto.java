package project.atch.domain.user.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ResponseItemDto {
    private String characterImage; // 현재 캐릭터의 이미지
    private List<String> itemImage; // 사용자가 가지고 있는 아이템 이미지 리스트
    private double itemX; // 현재 캐릭터의 아이템 x 위치
    private double itemY; // 현재 캐릭터의 아이템 y 위치


    public ResponseItemDto(String characterImage, List<String> itemImage, double itemX, double itemY){
        this.characterImage = characterImage;
        this.itemImage = itemImage;
        this.itemX = itemX;
        this.itemY = itemY;
    }


}
