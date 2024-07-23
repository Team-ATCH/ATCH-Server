package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.ItemDetail;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.ItemRepository;
import project.atch.domain.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersDetail() {
        List<User> users = userRepository.findAllByLocationPermissionTrue();
        return users.stream()
                .filter(User::isInHongdae)
                .map(this::mapToUserDetailDto)
                .collect(Collectors.toList());
    }

    private UserDetailDto mapToUserDetailDto(User user) {
        List<ItemDetail> items = getItemDetails(user);
        return new UserDetailDto(user, user.getCharacter(), items);
    }

    private List<ItemDetail> getItemDetails(User user) {
        Long[] itemIds = {user.getItemId1(), user.getItemId2(), user.getItemId3()};

        return Arrays.stream(itemIds)
                .map(this::findItemDetailById)
                .collect(Collectors.toList());
    }

    private ItemDetail findItemDetailById(Long itemId) {
        if (itemId != null) {
            Optional<Item> item = itemRepository.findById(itemId);
            if (item.isPresent()) {
                return new ItemDetail(item.get().getId(), item.get().getImage());
            }
        }
        return new ItemDetail(null, null);
    }

    @Transactional
    public void updateUserLocation(long userId, double latitude, double longitude){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateLocation(latitude, longitude);
    }

}
