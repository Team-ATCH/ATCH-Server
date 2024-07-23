package project.atch.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoomFormDto {

    public record Req(
            @NotNull @JsonProperty("userId") Long userId
    ) {
    }

    public record Res(
            long roomId,
            long fromId,
            long toId
    ) {
    }
}
