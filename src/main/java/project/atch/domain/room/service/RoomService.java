package project.atch.domain.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.dto.RoomFormDto;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.UserRepository;
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


        Room room = Room.builder()
                .fromId(userId)
                .toId(toUser.getId()).build();
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





}
