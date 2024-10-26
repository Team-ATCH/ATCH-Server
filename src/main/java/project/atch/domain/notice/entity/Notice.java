package project.atch.domain.notice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.atch.domain.user.entity.ItemName;
import project.atch.domain.user.entity.User;
import project.atch.global.entity.BaseEntity;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    private String title;

    private String content;

    private boolean isItem;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notice(String title, String content, User user, boolean isItem){
        this.title = title;
        this.content = content;
        this.user = user;
        this.isItem = isItem;
    }

    public static Notice of(ItemName item, User user){
        String contentTemplate = "지급 조건 '%s'을 달성해 '%s' 아이템을 획득했어요. 지금 바로 이 아이템을 장착해 보세요.";
        String content = String.format(contentTemplate, item.getTerm(), item.getName());

        return Notice.builder()
                .title(String.format("'%s' 아이템 도착!", item.getName()))
                .content(content)
                .user(user)
                .isItem(true)
                .build();
    }
}
