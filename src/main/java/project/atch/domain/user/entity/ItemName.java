package project.atch.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemName {

    LOVELY("러블리", "atch 시작하기",1),
    ONLINE("온라인","atch 시작하기", 2),
    PARTY_POPPERS("환영", "atch 시작하기",3),
    FIRST_MESSAGE("선톡", "첫 채팅 보내기",4),
    POKE("쿡 찌르기", "채팅 5번 보내기",5),
    GOOD_IMPRESSION("좋은 인상", "atch에서 5명과 대화하기",6),
    CHATTERBOX("수다쟁이", "채팅 20번 보내기",7),
    SOCIAL_BUTTERFLY("인싸", "atch에서 10명과 대화하기",8),
    GRAND_ENTRANCE("이 몸 등장", "캐릭터 바꾸기",9),
    ONE_PLUS_ONE("원 플러스 원", "첫 아이템 장착하기",10),
    THATS_A_BIT("그건 좀", "불쾌감을 주는 사용자를 차단하기",11),
    BUG_FIX("오류 제거", "불쾌감을 주는 사용자를 세 명 차단하기",12);

    private final String name;
    private final String term;
    private final long value;
}
