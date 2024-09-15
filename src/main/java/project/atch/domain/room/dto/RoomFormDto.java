package project.atch.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class RoomFormDto {

    public record RoomReq(
            @NotNull @JsonProperty("userId") Long userId
    ) {
    }

    public record RoomRes(
            long roomId,
            long fromId,
            long toId
    ) {
    }
}
