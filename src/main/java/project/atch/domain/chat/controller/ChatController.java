package project.atch.domain.chat.controller;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import project.atch.domain.chat.dto.MessageDto;
import project.atch.domain.chat.service.ChatService;
import project.atch.global.dto.SuccessResponse;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/messages")
@Tag(name = "채팅 메세지 송수신 및 조회 API")
public class ChatController {

    private final ChatService chatService;

    //메세지 송신 및 수신
    @MessageMapping("/messages/{roomId}")
    public Mono<ResponseEntity<Void>> receiveMessage(@DestinationVariable Long roomId,
                                                     @RequestBody @Valid MessageDto.Req chat,
                                                     SimpMessageHeaderAccessor headerAccessor) {
        // 세션에서 사용자 ID 가져오기
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_IN_SESSION);
        }
        Long userId = (Long) sessionAttributes.get("userId"); // 송신자 Id

        return chatService.handleMessage(roomId, chat.content(), userId)
                .map(savedChat -> ResponseEntity.ok().build());
    }

    @Operation(summary = "특정 채팅방의 모든 채팅 내용 조회",
            description = "특정 채팅방의 모든 채팅 메세지를 조회할 수 있습니다. 메세지는 최신순입니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "채팅방 아이디"),
    })
    @GetMapping("/{roomId}")
    public Mono<SuccessResponse<List<MessageDto.Res>>> find(@PathVariable("roomId") Long id) {
        Flux<MessageDto.Res> response = chatService.findAllMessages(id);
        return response.collectList().map(SuccessResponse::of);
    }

}
