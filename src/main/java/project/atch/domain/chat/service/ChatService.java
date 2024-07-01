package project.atch.domain.chat.service;

import java.util.Date;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.chat.dto.PreviewMessageDto;
import project.atch.domain.chat.dto.ResponseMessageDto;
import project.atch.domain.chat.entity.Chat;
import project.atch.domain.chat.repository.ChatRepository;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.User;
import project.atch.domain.user.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;



@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
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
            if (room.getFromId() != userId && room.getToId() != userId) {
                throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
            }
            log.info("[ChatService] 메세지 송신: {}", content);

            return room;
        }).flatMap(room -> saveChatMessage(roomId, content, userId));
    }



    public void sendMessageToSubscribers(Long roomId, String content) {
        template.convertAndSend("/sub/message/" + roomId, content);
    }


    @Transactional(readOnly = true)
    public Flux<ResponseMessageDto> findAllMessages(Long roomId) {
        // 해당 채팅방의 모든 채팅 메시지를 비동기적으로 조회
        Flux<Chat> chats = chatRepository.findAllByRoomIdOrderByCreatedAtDesc(roomId)
                .subscribeOn(Schedulers.boundedElastic());

        // 각 채팅 메시지에 대해 닉네임을 조회하고, ResponseMessageDto로 변환
        return chats.flatMap(chatMessage ->
                Mono.fromCallable(() -> {
                    Optional<User> user = userRepository.findById(chatMessage.getFromId());
                    String nickname = user.isPresent() ? user.get().getNickname() : "탈퇴한 사용자";
                    return ResponseMessageDto.of(chatMessage, nickname);
                }).subscribeOn(Schedulers.boundedElastic())
        );
    }


    @Transactional(readOnly = true)
    public Flux<PreviewMessageDto> findOldestMessagesFromAllRooms(int limit, long lastId) {
        // 각 채팅방에서 가장 오래된 메시지를 조회
        Flux<Chat> chats = chatRepository.findOldestMessagesFromAllRooms(limit, lastId)
                .subscribeOn(Schedulers.boundedElastic());

        // 각 채팅 메시지에 대해 닉네임을 조회하고, PreviewMessageDto로 변환
        return chats.flatMap(chatMessage ->
                Mono.fromCallable(() -> {
                    Optional<User> user = userRepository.findById(chatMessage.getFromId());
                    String nickname = user.isPresent() ? user.get().getNickname() : "탈퇴한 사용자";
                    return PreviewMessageDto.of(chatMessage, nickname);
                }).subscribeOn(Schedulers.boundedElastic())
        );
    }


}
