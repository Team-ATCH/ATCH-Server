package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.ResponseCharacterDto;
import project.atch.domain.user.entity.Character;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.CharacterRepository;
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
}
