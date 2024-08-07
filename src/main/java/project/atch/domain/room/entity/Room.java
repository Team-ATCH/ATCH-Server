package project.atch.domain.room.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.atch.global.entity.BaseEntity;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fromId", "toId"})
})
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long fromId;

    @NotNull
    @Column(nullable = false)
    private Long toId;

    @Builder
    public Room(Long fromId, Long toId){
        // 항상 fromId < toId로 설정
        if (fromId > toId) {
            this.fromId = toId;
            this.toId = fromId;
        } else {
            this.fromId = fromId;
            this.toId = toId;
        }
    }

}
