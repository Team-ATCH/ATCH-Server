package project.atch.domain.festival;

import lombok.Data;

@Data
public class FestivalDto {
    private String title;
    private String addr;
    private String eventStartDate;
    private String eventEndDate;
    private String firstImage;
    private Double mapx;
    private Double mapy;
}

