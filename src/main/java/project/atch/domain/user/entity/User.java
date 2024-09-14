package project.atch.domain.user.entity;

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
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "oAuthProvider"}),
        @UniqueConstraint(columnNames = {"nickname"})
})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String nickname;
    private String hashTag;

    private Boolean locationPermission;
    private Double latitude; // 위도
    private Double longitude; // 경도


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(5)", nullable = false)
    private OAuthProvider oAuthProvider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "character_id")
    private Character character;

    // 사용자가 착용중인 아이템들 아이디
    private Long itemId1;
    private Long itemId2;
    private Long itemId3;

    private String fcmToken;

    @Column(columnDefinition = "int default 0")
    private int chatCnt; // 메세지 보낸 횟수

    @Column(columnDefinition = "int default 0")
    private int changeCnt; // 캐릭터 바꾼 횟수

    @Builder
    private User(OAuthProvider oAuthProvider, String nickname, String email){
        this.oAuthProvider = oAuthProvider;
        this.nickname = nickname;
        this.email = email;
        this.role = Role.ROLE_USER;
    }

    public static User fromKakao(String nickname, String email){
        return User.builder()
                .oAuthProvider(OAuthProvider.KAKAO)
                .nickname(nickname)
                .email(email).build();
    }


    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateCharacter(Character character){
        this.character = character;
    }

    public void updateHashTag(String hashTag){
        this.hashTag = hashTag;
    }

    public void updateItems(Long itemId1, Long itemId2, Long itemId3){
        this.itemId1 = itemId1;
        this.itemId2 = itemId2;
        this.itemId3 = itemId3;
    }

    public void updateChatCnt(){
        this.chatCnt++; // TODO
    }

    public void updateChangeCnt(){
        this.changeCnt++; // TODO
    }

}