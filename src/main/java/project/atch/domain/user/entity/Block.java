package project.atch.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.atch.global.entity.BaseEntity;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"blockerId", "blockedId"})
})
public class Block extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "block_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long blockerId; // 차단한 사용자 계정

    @NotNull
    @Column(nullable = false)
    private Long blockedId; // 차단 당한 사용자 계정

    public Block(Long blockerId, Long blockedId){
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

}
