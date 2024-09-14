package project.atch.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.room.dto.MyMessagePreviewDto;
import project.atch.domain.room.dto.OtherMessagePreviewDto;
import project.atch.domain.chat.entity.Chat;
import project.atch.domain.chat.repository.ChatRepository;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.dto.RoomFormDto;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.entity.ItemNumber;
import project.atch.domain.user.entity.Notice;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.BlockRepository;
import project.atch.domain.user.repository.NoticeRepository;
import project.atch.domain.user.repository.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final BlockRepository blockRepository;
    private final NoticeRepository noticeRepository;

    // 채팅방 생성
    @Transactional
    public ResponseEntity<RoomFormDto.Res> createRoom(Long fromId, Long toId) {
        User toUser = userRepository.findById(toId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        User fromUser = userRepository.findById(fromId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));

        // 아이디 정렬하기
        if(toId < fromId){
            Long tmp = fromId;
            fromId = toId;
            toId = tmp;
        }

        // 있는지 확인
        Optional<Room> existingRoom = roomRepository.findByFromIdAndToId(fromId, toId);
        if (existingRoom.isPresent()) {
            Room room = existingRoom.get();
            return new ResponseEntity(new RoomFormDto.Res(room.getId(), fromId, toId), HttpStatus.OK);
        }

        // 이미 존재하는 채팅방이 없다면 생성 후 리턴
        Room room = createAndSaveRoom(fromId, toId);
        log.info("[RoomService-createRoom] {}, {} room 엔티티 생성" ,fromId, toUser.getId());

        grantItemsForRoom(fromUser);
        grantItemsForRoom(toUser);

        return new ResponseEntity(new RoomFormDto.Res(room.getId(), fromId, toId), HttpStatus.CREATED);
    }

    private void grantItemsForRoom(User user){
        int cnt = roomRepository.countByFromIdOrToId(user.getId(), user.getId());
        Notice notice;
        switch (cnt){
            case 5:
                notice = Notice.of(ItemNumber.CHATTERBOX, user);
                noticeRepository.save(notice);
                break;
            case 10:
                notice = Notice.of(ItemNumber.SOCIAL_BUTTERFLY, user);
                noticeRepository.save(notice);
                break;
        }
    }

    private Room createAndSaveRoom(Long fromId, Long toId) {
        Room room = Room.builder()
                .fromId(fromId)
                .toId(toId)
                .build();
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Flux<MyMessagePreviewDto> getAllMyRooms(long userId){
        // 사용자가 속한 roomId 목록을 조회, 차단한 사용자가 속한 roomId는 제외
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        List<Long> blockedId = blockRepository.findBlockedIdsByBlockerId(userId);

        List<Room> rooms = roomRepository.findFilteredRooms(userId, blockedId);

        for(Room room: rooms) log.info("{}", room.getId());

        // 각 roomId에 해당하는 가장 최신 메시지를 조회
        return Flux.fromIterable(rooms)
                .flatMap(room -> chatRepository.findTopByRoomIdOrderByCreatedAtDesc(room.getId())
                        .flatMap(chat -> {
                            return Mono.just(MyMessagePreviewDto.of(chat, user));
                        })
                )
                // 최신순으로 정렬
                .sort(Comparator.comparing(MyMessagePreviewDto::getCreatedAt).reversed());
    }

    @Transactional(readOnly = true)
    public Flux<OtherMessagePreviewDto> getAllRoomsWithPreviews(int limit, long lastId, long userId) {
        // 제외할 사용자 id 조회
        List<Long> blockedId = getBlockedRoomIds(userId);

        // 각 채팅방에서 가장 오래된 메시지를 조회
        Flux<Chat> chats = chatRepository.findOldestMessagesFromAllRooms(limit, lastId, blockedId)
                .subscribeOn(Schedulers.boundedElastic());

        // 각 채팅 메시지에 대해 닉네임을 조회하고, OtherMessagePreviewDto로 변환
        return chats.flatMap(chatMessage ->
                Mono.fromCallable(() -> {
                    Optional<User> user = userRepository.findById(chatMessage.getFromId());
                    String nickname = user.isPresent() ? user.get().getNickname() : "탈퇴한 사용자";
                    return OtherMessagePreviewDto.of(chatMessage, nickname);
                }).subscribeOn(Schedulers.boundedElastic())
        );
    }


    private List<Long> getBlockedRoomIds(Long userId) {
        // 차단된 사용자 목록을 조회
        List<Long> blockedIds = blockRepository.findBlockedIdsByBlockerId(userId);

        // 차단된 사용자 ID 목록을 기반으로 방 ID 조회
        return roomRepository.findRoomIdsByUserIds(blockedIds);
    }





}
