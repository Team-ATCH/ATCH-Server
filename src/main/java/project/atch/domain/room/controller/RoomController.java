package project.atch.domain.room.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.room.dto.RoomFormDto;
import project.atch.domain.room.service.RoomService;
import project.atch.global.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /**
     * 채팅방 등록
     */
    @PostMapping("/room")
    public ResponseEntity createRoom(@RequestBody RoomFormDto form){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long userId = principal.getUserId();

        roomService.createRoom(userId,form);
        // 그러면 상대방은 자동으로 sub/room/{roomId}로 구독 시작해야함
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
