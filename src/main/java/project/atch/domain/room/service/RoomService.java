package project.atch.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.dto.RoomFormDto;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.User;
import project.atch.domain.user.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    /**
     * 채팅방 생성
     */
    public void createRoom(Long userId, RoomFormDto dto) {
        User toUser = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        User fromUser = userRepository.findById(userId)
                .orElseThrow();

        // TODO 이미 존재하는 방이 있는지 확인
        Room room = Room.builder()
                .fromUser(fromUser)
                .toUser(toUser).build();
        roomRepository.save(room);

        log.info("[RoomService-createRoom] {}, {} room 엔티티 생성" ,userId, toUser.getId());
    }

    /**
     * 모든 채팅방 찾기
     */
    public List<Room> findAllRoom() {
        return roomRepository.findAll();
    }

    /**
     * 내 채팅방 찾기
     */


    /**
     * 채팅방에 속한 유저인지 검증
     */
    public void validateUserInRoom(Long userId, Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow();

        if (userId != room.getToUser().getId() && userId != room.getFromUser().getId()){
            throw new CustomException(ErrorCode.USER_NOT_IN_CHAT_ROOM);
        }
    }


}
