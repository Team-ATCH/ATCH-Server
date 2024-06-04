package project.atch.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.chat.dto.ChatDto;
import project.atch.domain.chat.service.ChatService;
import project.atch.domain.room.service.RoomService;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final RoomService roomService;

    // 메세지 발행 시 매핑 되는 주소
    // 클라이언트 측에서는 /pub/room/{roomId}에 메세지를 보내면 됨
    @MessageMapping("/room/{roomId}")
    public void handleMessage(@DestinationVariable Long roomId, ChatDto message, SimpMessageHeaderAccessor headerAccessor) {

        // 세션에서 사용자 ID 가져오기
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND_IN_SESSION);
        }
        Long userId = (Long) sessionAttributes.get("userId");

        // 채팅방에 사용자 있는지 검증
        roomService.validateUserInRoom(userId, roomId);

        // 채팅 보내기
        chatService.createAndSendChat(roomId, message.getMessage(), userId);
    }
}
