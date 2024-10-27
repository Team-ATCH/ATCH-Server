package project.atch.domain.chat.service;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.chat.dto.MessageDto;
import project.atch.domain.chat.entity.Chat;
import project.atch.domain.chat.repository.ChatRepository;
import project.atch.domain.notice.service.NoticeService;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.entity.*;
import project.atch.domain.user.repository.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.stomp.RoomUserCountManager;
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

    private final NoticeService noticeService;

    @Transactional
    public Mono<Void> handleMessage(Long roomId, String content, Long userId) {
        return validateRoom(roomId, userId)
                .flatMap(room -> saveChatMessage(roomId, content, userId))
                .flatMap(savedChat -> {
                    sendMessageToSubscribers(roomId, savedChat);
                    grantItemsForChat(userId);

                    return Mono.empty();
                });
    }

    @Transactional
    public Mono<Chat> saveChatMessage(Long roomId, String content, Long fromUserId) {
        return chatRepository.save(
                new Chat(roomId, content, fromUserId, new Date(), true));
    }

    private void sendMessageToSubscribers(Long roomId, Chat savedChat) {
        template.convertAndSend("/sub/messages/" + roomId, savedChat);
    }

    private void grantItemsForChat(long userId){
        // 아이템 지급
        User fromUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        fromUser.updateChatCnt();
        switch (fromUser.getChatCnt()){
            case 1:
                noticeService.createItemNotice(fromUser, ItemName.FIRST_MESSAGE);
                break;
            case 5:
                noticeService.createItemNotice(fromUser, ItemName.POKE);
                break;
            case 20:
                noticeService.createItemNotice(fromUser, ItemName.GOOD_IMPRESSION);
                break;
        }
    }

    private long getAntherId(Long roomId, Long userId){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (room.getFromId() != userId) return room.getFromId();
        return room.getToId();
    }

    private Mono<Room> validateRoom(Long roomId, Long userId) {
        return Mono.fromCallable(() -> {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

            if (room.getFromId() != userId && room.getToId() != userId) {
                throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
            }
            return room;
        });
    }


    @Transactional(readOnly = true)
    public Flux<MessageDto.Res> findAllMessages(Long roomId) {
        // 해당 채팅방의 모든 채팅 메시지를 비동기적으로 조회
        Flux<Chat> chats = chatRepository.findAllByRoomIdOrderByCreatedAtDesc(roomId)
                .subscribeOn(Schedulers.boundedElastic());

        // 각 채팅 메시지에 대해 닉네임을 조회하고, ResponseMessageDto로 변환
        return chats.flatMap(chatMessage ->
                Mono.fromCallable(() -> {
                    Optional<User> user = userRepository.findById(chatMessage.getFromId());
                    String nickname = user.isPresent() ? user.get().getNickname() : "탈퇴한 사용자";
                    return MessageDto.Res.of(chatMessage, nickname);
                }).subscribeOn(Schedulers.boundedElastic())
        );
    }

}
