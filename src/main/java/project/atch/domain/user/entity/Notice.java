package project.atch.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notice(String title, String content, User user){
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static Notice of(ItemNumber item, User user){
        String content = "임시입니다.";

        return Notice.builder()
                .title(String.format("'%s' 아이템 도착!", item.getName()))
                .content(content)
                .user(user)
                .build();
    }
}
