package project.atch.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "atch_character")
public class Character {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "character_id")
    private Long id;

    private String image;

    // 캐릭터마다 아이템을 놓을 수 있는 위치가 3개씩 존재
    private double x1;
    private double y1;

    private double x2;
    private double y2;

    private double x3;
    private double y3;

}
