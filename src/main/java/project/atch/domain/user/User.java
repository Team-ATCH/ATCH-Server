package project.atch.domain.user;

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
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String nickname;
    private String hashTag;

    private Boolean locationPermission;
    private Double latitude; // 위도
    private Double longitude; // 경도

    // TODO 캐릭터, 아이템 고려

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(5)")
    private OAuthProvider oAuthProvider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private Role role;

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

}