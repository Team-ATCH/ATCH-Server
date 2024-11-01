package project.atch.domain.room.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.atch.domain.room.dto.MyMessagePreviewDto;
import project.atch.domain.room.dto.OtherMessagePreviewDto;
import project.atch.domain.room.dto.RoomFormDto;
import project.atch.domain.room.service.RoomService;
import project.atch.global.dto.SuccessResponse;
import project.atch.global.security.CustomUserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/rooms")
@Tag(name = "채팅방 등록 및 전체 채팅방 미리보기 조회 API")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "채팅방 등록",
            description = "채팅하려는 사용자의 아이디를 받아 새로운 채팅방의 아이디를 생성합니다.")
    @PostMapping
    public ResponseEntity<RoomFormDto.RoomRes> createRoom(@RequestBody @Valid RoomFormDto.RoomReq form,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails){
        // 응답 내려주면 요청한 사용자는 sub/message/{roomId}로 구독 시작해야함
        return roomService.createRoom(userDetails.getUserId(), form.userId());
    }

    @Operation(summary = "내 채팅",
            description = "자신이 속한 채팅방과 상대방의 프로필을 확인합니다.")
    @Parameters({
            @Parameter(name = "limit", description = "한 페이지 당 몇 개의 데이터를 가져올 지"),
            @Parameter(name = "lastId", description = "마지막 채팅방 아이디")
    })
    @GetMapping("/active")
    public Mono<SuccessResponse<List<MyMessagePreviewDto>>> findAllMyRooms(@AuthenticationPrincipal CustomUserDetails userDetails){
        return roomService.getAllMyRooms(userDetails.getUserId())
                .collectList()  // Flux를 List로 변환
                .map(SuccessResponse::of);
    }


    @Operation(summary = "전체 채팅방 + 미리보기 조회",
            description = "전체 채팅방과 각 채팅방의 첫 번째 채팅 메세지를 확인합니다. "
                    + "커서 기반 페이지네이션으로 작동합니다.")
    @Parameters({
            @Parameter(name = "limit", description = "한 페이지 당 몇 개의 데이터를 가져올 지"),
            @Parameter(name = "lastId", description = "마지막 채팅방 아이디")
    })
    @GetMapping
    public Mono<SuccessResponse<List<OtherMessagePreviewDto>>> findAllRooms(@RequestParam(defaultValue = "10") int limit,
                                                                           @RequestParam(defaultValue = "-1") long lastId,
                                                                           @AuthenticationPrincipal CustomUserDetails userDetails){
        return roomService.getAllRoomsWithPreviews(limit, lastId, userDetails.getUserId())
                .collectList()
                .map(SuccessResponse::of);
    }
}
