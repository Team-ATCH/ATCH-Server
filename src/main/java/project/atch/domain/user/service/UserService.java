package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.ResponseCharacterDto;
import project.atch.domain.user.entity.Character;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.entity.UserItem;
import project.atch.domain.user.repository.CharacterRepository;
import project.atch.domain.user.repository.ItemRepository;
import project.atch.domain.user.repository.UserItemRepository;
import project.atch.domain.user.repository.UserRepository;
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

    @Transactional
    public void updateItem(long userId, long itemId) {
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        UserItem used = userItemRepository.findByUserAndUsed(user, true).orElseThrow();// 현재 착용중인 아이템
        UserItem newOne = userItemRepository.findByUserAndItem(user, item).orElseThrow(); // 착용할 아이템

        used.switchedUsed();
        newOne.switchedUsed();
    }
}
