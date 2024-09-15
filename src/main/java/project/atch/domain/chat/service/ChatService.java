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
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.entity.*;
import project.atch.domain.user.repository.ItemRepository;
import project.atch.domain.user.repository.NoticeRepository;
import project.atch.domain.user.repository.UserItemRepository;
import project.atch.domain.user.repository.UserRepository;
import project.atch.domain.user.service.ItemService;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.fcm.FCMPushRequestDto;
import project.atch.global.fcm.FCMService;
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
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final NoticeRepository noticeRepository;
    private final RoomUserCountManager countManager;
    private final SimpMessageSendingOperations template;

    private final FCMService fcmService;

    @Transactional
    public Mono<Void> handleMessage(Long roomId, String content, Long userId) {
        return validateRoom(roomId, userId)
                .flatMap(room -> saveChatMessage(roomId, content, userId))
                .flatMap(savedChat -> {
                    int userCount = countManager.getUserCount(roomId);

                    // 사용자 인원에 따른 처리 분기
                    if (userCount == 2) {
                        // 사용자 인원이 2명인 경우 메시지 전송
                        sendMessageToSubscribers(roomId, savedChat);
                    } else if (userCount == 1) {
                        // 사용자 인원이 1명인 경우 FCM 알람 전송
                        long toUserId = getAntherId(roomId, userId);
                        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
                        FCMPushRequestDto dto = FCMPushRequestDto.makeChatAlarm(toUser.getFcmToken(), savedChat.getContent(), savedChat.getContent());
                        try {
                            fcmService.pushAlarm(dto);
                        } catch (IOException e) {
                            return Mono.error(new CustomException(ErrorCode.FCM_SERVER_COMMUNICATION_FAILED));
                        }
                    }
                    grantItemsForChat(userId);

                    return Mono.empty();
                });
    }

    private void grantItemsForChat(long userId){
        // 아이템 지급
        User fromUser = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        fromUser.updateChatCnt();
        switch (fromUser.getChatCnt()){
            case 1:
                createAndSaveNotice(fromUser, ItemNumber.FIRST_MESSAGE);
                grantItem(fromUser, ItemNumber.FIRST_MESSAGE);
                break;
            case 5:
                createAndSaveNotice(fromUser, ItemNumber.POKE);
                grantItem(fromUser, ItemNumber.POKE);
                break;
            case 20:
                createAndSaveNotice(fromUser, ItemNumber.GOOD_IMPRESSION);
                grantItem(fromUser, ItemNumber.GOOD_IMPRESSION);
                break;
        }
    }

    private void createAndSaveNotice(User user, ItemNumber itemNumber) {
        Notice notice = Notice.of(itemNumber, user);
        noticeRepository.save(notice);
    }

    private void grantItem(User user, ItemNumber itemNumber) {
        Item item = itemRepository.findById(itemNumber.getValue()).orElseThrow();
        UserItem userItem = new UserItem(user, item);
        userItemRepository.save(userItem);
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

    @Transactional
    public Mono<Chat> saveChatMessage(Long roomId, String content, Long fromUserId) {
        boolean read = countManager.getUserCount(roomId) == 2;

        return chatRepository.save(
                new Chat(roomId, content, fromUserId, new Date(), read));
    }

    public void sendMessageToSubscribers(Long roomId, Chat savedChat) {
        template.convertAndSend("/sub/messages/" + roomId, savedChat);
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
