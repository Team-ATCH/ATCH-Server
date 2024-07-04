package project.atch.domain.user.dto;

import lombok.Getter;

@Getter
public class RequestLocationDto {

    private final double latitude;
    private final double longitude;

    public RequestLocationDto(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
