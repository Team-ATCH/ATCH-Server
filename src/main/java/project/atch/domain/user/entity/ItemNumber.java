package project.atch.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemNumber {

    LOVELY("러블리", 1),
    ONLINE("온라인", 2),
    PARTY_POPPERS("축하 폭죽", 3),
    FIRST_MESSAGE("선톡", 4),
    POKE("쿡 찌르기", 5),
    GOOD_IMPRESSION("좋은 인상", 6),
    CHATTERBOX("수다쟁이", 7),
    SOCIAL_BUTTERFLY("인싸", 8);

    private final String name;
    private final long value;
}
