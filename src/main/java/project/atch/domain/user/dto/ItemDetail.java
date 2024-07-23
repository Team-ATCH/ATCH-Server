package project.atch.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ItemDetail {
    private Long itemId;
    private String itemImage;

    public ItemDetail(Long itemId, String itemImage){
        this.itemId = itemId;
        this.itemImage = itemImage;
    }
}
