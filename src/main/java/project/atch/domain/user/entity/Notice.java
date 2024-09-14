package project.atch.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
}
