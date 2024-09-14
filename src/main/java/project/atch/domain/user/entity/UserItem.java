package project.atch.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Entity
@Getter
@IdClass(UserItemId.class)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserItem {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Builder
    public UserItem(User user, Item item) {
        this.user = user;
        this.item = item;
    }

}
