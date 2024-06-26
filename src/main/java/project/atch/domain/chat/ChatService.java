package project.atch.domain.chat;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final SimpMessageSendingOperations template;

    @Transactional
    public Mono<Chat> saveChatMessage(Long roomId, String content, Long fromUserId) {
        return chatRepository.save(
                new Chat(roomId, content, fromUserId, new Date()));
    }

    public Mono<Chat> processMessage(Long roomId, String content, Long userId) {
        return Mono.fromCallable(() -> {
            // 채팅방에 사용자 있는지 검증
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
            if (room.getFromUserId() != userId && room.getToUserId() != userId) {
                throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
            }

            return room;
        }).flatMap(room -> saveChatMessage(roomId, content, userId));
    }

    public void sendMessageToSubscribers(Long roomId, String content) {
        template.convertAndSend("/sub/message/" + roomId, content);
    }

    @Transactional
    public Flux<ResponseMessageDto> findChatMessages(Long id) {
        Flux<Chat> chatMessages = chatRepository.findAllByRoomId(id);
        return chatMessages.map(ResponseMessageDto::of);
    }
}
