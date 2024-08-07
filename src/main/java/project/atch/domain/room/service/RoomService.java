package project.atch.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.chat.dto.PreviewMessageDto;
import project.atch.domain.chat.entity.Chat;
import project.atch.domain.chat.repository.ChatRepository;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.dto.RoomFormDto;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;

    // 채팅방 생성
    @Transactional
    public ResponseEntity<RoomFormDto.Res> createRoom(Long fromId, Long toId) {
        User toUser = userRepository.findById(toId)
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
        return new ResponseEntity(new RoomFormDto.Res(room.getId(), fromId, toId), HttpStatus.CREATED);
    }

    private Room createAndSaveRoom(Long fromId, Long toId) {
        Room room = Room.builder()
                .fromId(fromId)
                .toId(toId)
                .build();
        return roomRepository.save(room);
    }

    // 모든 채팅방 찾기
    @Transactional(readOnly = true)
    public List<Room> findAllRoom() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Flux<PreviewMessageDto> getAllRoomsWithPreviews(int limit, long lastId) {
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
