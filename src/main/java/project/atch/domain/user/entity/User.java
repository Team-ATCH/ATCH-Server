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


    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(5)")
    private OAuthProvider oAuthProvider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

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
    public boolean isInHongdae(){
        // TODO 임의로 위도 경도 지정. 기획 측에 문의해봐야함.
        if (37.546856 <= latitude && latitude <= 37.566418 && 126.907221 <= longitude && longitude <= 126.933994){
            return true;
        }
        return false;
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

}