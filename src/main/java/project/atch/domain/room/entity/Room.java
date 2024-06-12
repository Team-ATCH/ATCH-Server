package project.atch.domain.room.entity;

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
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private Long fromUserId;

    private Long toUserId;

    @Builder
    public Room(Long fromUserId, Long toUserId){
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

}
