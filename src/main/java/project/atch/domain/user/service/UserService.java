package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.ItemDetail;
import project.atch.domain.user.dto.ItemDto;
import project.atch.domain.user.dto.ResponseCharacterDto;
import project.atch.domain.user.entity.Block;
import project.atch.domain.user.entity.Character;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.*;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final UserItemRepository userItemRepository;
    private final ItemRepository itemRepository;
    private final BlockRepository blockRepository;

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
    public ItemDto.Res getAllItems(long userId){
        User user = userRepository.findById(userId).orElseThrow();
        List<ItemDetail> items = userItemRepository.findItemIdsAndImagesByUserId(userId);

        return new ItemDto.Res(user.getCharacter(), items);
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
    public void withdrawal(Long userId) {
        userItemRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    public void blockUser(Long blockerId, long blockedId) {
        Block block = new Block(blockerId, blockedId);
        blockRepository.save(block);

        Block reverseBlock = new Block(blockedId, blockerId);
        blockRepository.save(reverseBlock);
    }
}
