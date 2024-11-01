package project.atch.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {

    private final T data;

    public static <T> SuccessResponse<T> of(T data){
        return new SuccessResponse<>(data);
    }
}
