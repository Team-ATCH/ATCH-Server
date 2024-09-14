package project.atch.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ItemNumber {

    LOVELY("러블리", 1),
    ONLINE("온라인", 2),
    PARTY_POPPERS("축하 폭죽", 3);

    private final String name;
    private final long value;
}
