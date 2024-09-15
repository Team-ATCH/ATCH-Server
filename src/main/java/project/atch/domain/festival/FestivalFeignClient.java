package project.atch.domain.festival;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "FestivalClient", url = "https://apis.data.go.kr")
public interface FestivalFeignClient {

    @GetMapping("/B551011/KorService1/searchFestival1")
    String getFestivalData(@RequestParam("MobileOS") String mobileOS,
                           @RequestParam("MobileApp") String mobileApp,
                           @RequestParam("_type") String responseType,
                           @RequestParam("eventStartDate") String eventStartDate,
                           @RequestParam("areaCode") String areaCode,
                           @RequestParam("sigunguCode") String sigunguCode,
                           @RequestParam("serviceKey") String serviceKey);
}

