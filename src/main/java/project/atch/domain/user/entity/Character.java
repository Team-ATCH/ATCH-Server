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
    private double itemX;
    private double itemY;

}
