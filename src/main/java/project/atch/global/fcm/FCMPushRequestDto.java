package project.atch.global.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FCMPushRequestDto {

    private String targetToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String body;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;

    public static FCMPushRequestDto makeChatAlarm(String name, String content){
        return FCMPushRequestDto.builder()
                .title(name)
                .body(content)
                .build();
    }
}
