package project.atch.domain.festival;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalCache festivalCache;
    private final FestivalFeignClient festivalFeignClient;

    @Value("${FESTIVAL_API_SERVICE_KEY}")
    private String serviceKey;

    public List<FestivalDto> getFestivalData() {
        LocalDate today = LocalDate.now();

        if (festivalCache.hasDataForToday(today)) {
            return festivalCache.getFestivalData(today);
        }

        // FeignClient를 사용하여 API 요청
        String response = festivalFeignClient.getFestivalData(
                "IOS",
                "atch",
                "json",
                today.toString().replace("-", ""),
                "1",
                "13",
                serviceKey
        );

        List<FestivalDto> festivalList = parseFestivalData(response);

        // 캐시에 데이터 저장
        festivalCache.setFestivalData(today, festivalList);

        return festivalList;
    }

    private List<FestivalDto> parseFestivalData(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            List<FestivalDto> festivalList = new ArrayList<>();
            for (JsonNode item : items) {
                FestivalDto festivalDto = new FestivalDto();
                festivalDto.setTitle(item.path("title").asText());
                festivalDto.setAddr(item.path("addr1").asText() + " " + item.path("addr2").asText());
                festivalDto.setEventStartDate(item.path("eventstartdate").asText());
                festivalDto.setEventEndDate(item.path("eventenddate").asText());
                festivalDto.setFirstImage(item.path("firstimage").asText());
                festivalDto.setMapx(Double.parseDouble(item.path("mapx").asText()));
                festivalDto.setMapy(Double.parseDouble(item.path("mapy").asText()));

                festivalList.add(festivalDto);
            }
            return festivalList;

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.PARSE_JSON_ERROR);
        }
    }
}
