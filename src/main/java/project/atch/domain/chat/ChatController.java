package project.atch.domain.chat;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.room.service.RoomService;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private final SimpMessageSendingOperations template;

    //메세지 송신 및 수신
    @MessageMapping("/message/{roomId}")
    public Mono<ResponseEntity<Void>> receiveMessage(@DestinationVariable Long roomId, @RequestBody RequestMessageDto chat, SimpMessageHeaderAccessor headerAccessor) {

        // 세션에서 사용자 ID 가져오기
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_IN_SESSION);
        }
        Long userId = (Long) sessionAttributes.get("userId");

        // TODO 채팅방에 사용자 있는지 검증

        return chatService.saveChatMessage(roomId, chat.getContent(), userId).flatMap(message -> {
            // 메시지를 해당 채팅방 구독자들에게 전송
            template.convertAndSend("/sub/message/" + roomId,
                    chat.getContent());
            return Mono.just(ResponseEntity.ok().build());
        });
    }

    // 이전 채팅 내용 조회
    @GetMapping("/find/chat/list/{id}")
    public Mono<ResponseEntity<List<ResponseMessageDto>>> find(@PathVariable("id") Long id) {
        Flux<ResponseMessageDto> response = chatService.findChatMessages(id);
        Mono<ResponseEntity<List<ResponseMessageDto>>> map = response.collectList().map(list -> ResponseEntity.ok(list));
        return map;
    }
}
