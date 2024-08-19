package project.atch.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.atch.domain.user.dto.RequestCharacterDto;
import project.atch.domain.user.dto.RequestHashTagDto;
import project.atch.domain.user.dto.RequestNicknameDto;
import project.atch.domain.user.dto.ResponseCharacterDto;
import project.atch.domain.user.service.UserService;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
@Tag(name = "온보딩 API", description = "온보딩에 사용되는 API입니다.")
public class OnBoardController {

    private final UserService userService;

    @Operation(summary = "모든 캐릭터 조회",
            description = "사용자가 캐릭터를 선택할 수 있도록 모든 캐릭터를 조회합니다.")
    @GetMapping("/character")
    public List<ResponseCharacterDto> getAllCharacters(){
        return userService.findAllCharacters();
    }

    @Operation(summary = "사용자의 캐릭터 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "characterId: 선택한 캐릭터의 아이디",
                    content = @Content(
                            schema = @Schema(implementation = RequestCharacterDto.class)
                    )
            )
    )
    @PostMapping("/character")
    public void chooseCharacter(@RequestBody RequestCharacterDto dto,
                                @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.TOKEN_VALIDATION_EXCEPTION);
        userService.updateCharacter(userDetails.getUserId(), dto.getCharacterId());
    }

    @Operation(summary = "사용자의 해시태그 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "hashTag: 선택한 해시태그을 담은 배열",
                    content = @Content(
                            schema = @Schema(implementation = RequestHashTagDto.class)
                    )
            )
    )
    @PostMapping("/hash-tag")
    public void chooseHashTag(@RequestBody RequestHashTagDto dto,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.TOKEN_VALIDATION_EXCEPTION);
        userService.updateHashTag(userDetails.getUserId(), dto.getHashTag());
    }

    @Operation(summary = "사용자의 닉네임 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "nickname: 닉네임",
                    content = @Content(
                            schema = @Schema(implementation = RequestNicknameDto.class)
                    )
            )
    )
    @PostMapping("/nickname")
    public void updateNickname(@RequestBody RequestNicknameDto dto,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.TOKEN_VALIDATION_EXCEPTION);
        userService.updateNickname(userDetails.getUserId(), dto.getNickname());
    }
}
