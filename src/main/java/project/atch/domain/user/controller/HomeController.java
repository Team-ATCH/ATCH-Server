package project.atch.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.atch.domain.festival.FestivalDto;
import project.atch.domain.festival.FestivalService;
import project.atch.domain.user.dto.NoticeResponse;
import project.atch.domain.user.dto.RequestLocationDto;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.service.HomeService;
import project.atch.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "지도 API", description = "지도 화면 및 다른 사용자 프로필 모달에 사용되는 API입니다.")
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;
    private final FestivalService festivalService;

    @Operation(summary = "마포구 내 진행 예정인 축제 조회",
            description = "마포구 내 진행 예정인 모든 축제를 조회합니다.")
    @GetMapping("/festival")
    public List<FestivalDto> getTodayFestivalDto(){
        return festivalService.getFestivalData();
    }

    @Operation(summary = "지도 내 위치하는 모든 사용자 조회",
            description = "현재 홍대 내 위치하는 모든 사용자의 일부 정보를 가져옵니다.")
    @GetMapping()
    public List<UserDetailDto> getUsersDetail(@AuthenticationPrincipal CustomUserDetails userDetails){
        return homeService.getUsersDetail(userDetails.getUserId());
    }

    @Operation(summary = "사용자의 위치 업데이트",
            description = "사용자의 위치를 업데이트합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "latitude: 사용자의 위도, longitude: 사용자의 경도",
                    content = @Content(
                            schema = @Schema(implementation = RequestLocationDto.class)
                    )
            )
    )
    @PatchMapping("/locate")
    public void updateUserLocation(@RequestBody @Valid RequestLocationDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        homeService.updateUserLocation(userDetails.getUserId(), dto.getLatitude(), dto.getLongitude());
    }

    @Operation(summary = "알람 조회")
    @GetMapping("/notice")
    public List<NoticeResponse> getNotice(@AuthenticationPrincipal CustomUserDetails userDetails){
        return homeService.getNotice(userDetails.getUserId());
    }
}
