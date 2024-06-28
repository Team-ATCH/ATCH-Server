package project.atch.domain.chat.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import project.atch.domain.chat.dto.PreviewMessageDto;
import project.atch.domain.chat.service.ChatService;
import project.atch.domain.chat.dto.RequestMessageDto;
import project.atch.domain.chat.dto.ResponseMessageDto;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    //메세지 송신 및 수신
    @MessageMapping("/message/{roomId}")
    public Mono<ResponseEntity<Void>> receiveMessage(@DestinationVariable Long roomId, @RequestBody RequestMessageDto chat, SimpMessageHeaderAccessor headerAccessor) {

        // 세션에서 사용자 ID 가져오기
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_IN_SESSION);
        }
        Long userId = (Long) sessionAttributes.get("userId");

        return chatService.processMessage(roomId, chat.getContent(), userId)
                .flatMap(message -> {
                    // 메시지를 해당 채팅방 구독자들에게 전송
                    chatService.sendMessageToSubscribers(roomId, chat.getContent());
                    return Mono.just(ResponseEntity.ok().build());
                });
    }

    // 이전 채팅 내용 조회
    @GetMapping("/message/list/{roomId}")
    public Mono<ResponseEntity<List<ResponseMessageDto>>> find(@PathVariable("roomId") Long id) {
        Flux<ResponseMessageDto> response = chatService.findAllMessages(id);
        Mono<ResponseEntity<List<ResponseMessageDto>>> map = response.collectList().map(list -> ResponseEntity.ok(list));
        return map;
    }

    // 전체 채팅방 + 각 첫 번째 채팅 조회
    @GetMapping("/message/list")
    public Mono<ResponseEntity<List<PreviewMessageDto>>> findLists(@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "-1") long lastId){
        return chatService.findOldestMessagesFromAllRooms(limit, lastId)
                .collectList()
                .map(ResponseEntity::ok);
    }


}
