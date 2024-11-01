package project.atch.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.atch.domain.user.dto.*;
import project.atch.domain.user.service.UserService;
import project.atch.global.dto.SuccessResponse;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "마이페이지 API", description = "마이페이지 및 온보딩에 사용되는 사용자 관련 API입니다.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "모든 캐릭터 조회",
            description = "사용자가 캐릭터를 선택할 수 있도록 모든 캐릭터를 조회합니다.")
    @GetMapping("/character")
    public SuccessResponse<List<ResponseCharacterDto>> getAllCharacters(){
        return SuccessResponse.of(userService.findAllCharacters());
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
    @PatchMapping("/character")
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
    @PatchMapping("/hash-tag")
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
    @PatchMapping("/nickname")
    public void updateNickname(@RequestBody RequestNicknameDto dto,
                              @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) throw new CustomException(ErrorCode.TOKEN_VALIDATION_EXCEPTION);
        userService.updateNickname(userDetails.getUserId(), dto.getNickname());
    }

    @Operation(summary = "모든 아이템 조회",
            description = "사용자가 아이템을 선택할 수 있도록 보유 중인 아이템을 조회합니다.")
    @GetMapping("/item")
    public ItemDto.ItemRes getAllItems(@AuthenticationPrincipal CustomUserDetails userDetails){
        return userService.getAllItems(userDetails.getUserId());
    }

    @Operation(summary = "사용자의 아이템 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "itemId: 아이템의 아이디",
                    content = @Content(
                            schema = @Schema(implementation = ItemDto.ItemReq.class)
                    )
            )
    )
    @PatchMapping("/item")
    public void updateItem(@RequestBody ItemDto.ItemReq dto,
                           @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.updateItems(userDetails.getUserId(), dto.itemId1(), dto.itemId2(), dto.itemId3());
    }

    @Operation(summary = "모든 아이템 조회",
            description = "사용자가 아이템을 선택할 수 있도록 보유 중인 아이템을 조회합니다.")
    @GetMapping("/background")
    public SuccessResponse<List<ItemDetail>> getAllBackGrounds(@AuthenticationPrincipal CustomUserDetails userDetails){
        return SuccessResponse.of(userService.getAllBackgrounds(userDetails.getUserId()));
    }

    @Operation(summary = "사용자의 배경 업데이트",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "backgroundId: 배경의 아이디",
                    content = @Content(
                            schema = @Schema(implementation = BackgroundReq.class)
                    )
            )
    )
    @PatchMapping("/background")
    public void updateBackground(@RequestBody BackgroundReq req,
                           @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.updateBackground(userDetails.getUserId(), req.backgroundId());
    }

    @Operation(summary = "사용자의 회원 탈퇴")
    @DeleteMapping()
    public ResponseEntity withdrawal(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.withdrawal(userDetails.getUserId());
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "특정 사용자를 차단")
    @PostMapping("/block")
    public ResponseEntity blockUser(@RequestBody RequestBlockDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.blockUser(userDetails.getUserId(), dto.getUserId());
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
