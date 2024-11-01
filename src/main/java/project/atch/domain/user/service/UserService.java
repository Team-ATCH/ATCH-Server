package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.notice.entity.Notice;
import project.atch.domain.notice.repository.NoticeRepository;
import project.atch.domain.notice.service.NoticeService;
import project.atch.domain.user.dto.ItemDetail;
import project.atch.domain.user.dto.ItemDto;
import project.atch.domain.user.dto.ResponseCharacterDto;
import project.atch.domain.user.entity.*;
import project.atch.domain.user.entity.Character;
import project.atch.domain.user.repository.*;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final UserItemRepository userItemRepository;
    private final ItemRepository itemRepository;
    private final BlockRepository blockRepository;

    private final NoticeService noticeService;

    @Transactional(readOnly = true)
    public List<ResponseCharacterDto> findAllCharacters(){
        return characterRepository.findAll()
                .stream()
                .map(ResponseCharacterDto::of)
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateCharacter(long userId, long characterId) {
        User user = userRepository.findById(userId).orElseThrow();

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));
        user.updateCharacter(character);
        user.updateChangeCnt();

        grantItemsForCharacter(user); // 아이템 지급
    }

    private void grantItemsForCharacter(User user){
        switch (user.getChangeCnt()){
            case 1:
                noticeService.createItemNotice(user, ItemName.GRAND_ENTRANCE);
                break;
            case 5:
                noticeService.createItemNotice(user, ItemName.ONE_PLUS_ONE);
                break;
        }
    }

    @Transactional
    public void updateHashTag(long userId, List<String> hashTag){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateHashTag(String.join(", ", hashTag));
    }

    @Transactional
    public void updateNickname(long userId, String nickname){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateNickname(nickname);
    }

    @Transactional(readOnly = true)
    public ItemDto.ItemRes getAllItems(long userId){
        User user = userRepository.findById(userId).orElseThrow();
        List<ItemDetail> items = userItemRepository.findItemIdsAndImagesByUserId(userId, ItemCategory.ITEM.toString());

        return new ItemDto.ItemRes(user.getCharacter(), items);
    }

    @Transactional(readOnly = true)
    public List<ItemDetail> getAllBackgrounds(long userId){
        return userItemRepository.findItemIdsAndImagesByUserId(userId , ItemCategory.BACKGROUND.toString());
    }

    @Transactional
    public void updateItems(long userId, Long itemId1, Long itemId2, Long itemId3) {
        User user = userRepository.findById(userId).orElseThrow();

        // 각 아이템 아이디를 검사하고 사용자가 보유한 아이템인지 확인하여 업데이트할 아이템 아이디를 결정
        Long updatedItemId1 = validateItem(userId, itemId1);
        Long updatedItemId2 = validateItem(userId, itemId2);
        Long updatedItemId3 = validateItem(userId, itemId3);

        // 사용자 엔티티를 업데이트
        user.updateItems(updatedItemId1, updatedItemId2, updatedItemId3);
    }

    private Long validateItem(long userId, Long itemId) {
        if (itemId != null && itemRepository.existsById(itemId) && userItemRepository.existsByUserIdAndItemId(userId, itemId)) {
            return itemId; // 만족하는 경우 해당 아이템 아이디를 반환
        } else {
            return null; // 만족하지 않는 경우 null을 반환
        }
    }

    @Transactional
    public void updateBackground(long userId, long backgroundId){
        User user = userRepository.findById(userId).orElseThrow();
        Optional.ofNullable(validateItem(userId, backgroundId))
                .ifPresentOrElse(
                        user::updateBackground,
                        () -> { throw new CustomException(ErrorCode.ITEM_NOT_OWNED); }
                );
    }

    /**
     * 사용자 탈퇴
     * @param userId 사용자 id
     */
    @Transactional
    public void withdrawal(Long userId) {
        noticeService.deleteUserData(userId); // 유저 관련 데이터를 전부 삭제
        userRepository.deleteById(userId); // 유저 데이터 삭제
    }

    /**
     * 사용자 차단
     * @param blockerId 차단을 요청하는 사용자
     * @param blockedId 차단 당하는 사용자
     */
    @Transactional
    public void blockUser(Long blockerId, long blockedId) {
        Block block = new Block(blockerId, blockedId);
        blockRepository.save(block);

        Block reverseBlock = new Block(blockedId, blockerId);
        blockRepository.save(reverseBlock);

        getItemsForBlocking(blockerId); // 아이템 지급
    }

    private void getItemsForBlocking(long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        user.updateBlockCnt();

        switch (user.getBlockCnt()){
            case 1:
                noticeService.createItemNotice(user, ItemName.THATS_A_BIT);
                break;
            case 3:
                noticeService.createItemNotice(user, ItemName.BUG_FIX);
                break;
        }
    }

}
