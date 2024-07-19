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

    // TODO 리팩토링
    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersDetail(){
        List<UserDetailDto> list = new ArrayList<>();
        List<User> users = userRepository.findAllByLocationPermissionTrue();
        for(User user: users){
            if (user.isInHongdae()){
                Long[] itemIds = {user.getItemId1(), user.getItemId2(), user.getItemId3()};

                List<ItemDetail> items = Arrays.stream(itemIds)
                        .map(itemId -> {
                            if (itemId != null) {
                                Optional<Item> item = itemRepository.findById(itemId);
                                if (item != null) {
                                    return new ItemDetail(item.get().getId(), item.get().getImage());
                                }
                            }
                            return new ItemDetail(null, null);
                        })
                        .collect(Collectors.toList());

                UserDetailDto userDetailDto = new UserDetailDto(user, user.getCharacter(), items);
                list.add(userDetailDto);
            }
        }
        return list;
    }

    @Transactional
    public void updateUserLocation(long userId, double latitude, double longitude){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateLocation(latitude, longitude);
    }

}
