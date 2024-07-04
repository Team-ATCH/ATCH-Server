package project.atch.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.user.dto.*;
import project.atch.domain.user.service.UserService;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/character")
    public List<ResponseCharacterDto> getAllCharacters(){
        return userService.findAllCharacters();
    }

    @PostMapping("/character")
    public void takeCharacter(@RequestBody RequestCharacterDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        userService.updateCharacter(userDetails.getUserId(), dto.getCharacterId());
    }

    @PostMapping("/hash-tag")
    public void chooseHashTag(@RequestBody RequestHashTagDto dto,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        userService.updateHashTag(userDetails.getUserId(), dto.getHashTag());
    }

    @PostMapping("/nickname")
    public void updateNickname(@RequestBody RequestNicknameDto dto,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        userService.updateNickname(userDetails.getUserId(), dto.getNickname());
    }

    @PostMapping("/item")
    public void updateItem(@RequestBody RequestItemDto dto,
                           @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.UNAUTHORIZED);
        userService.updateItem(userDetails.getUserId(), dto.getItemId());
    }

}
