package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersDetail(){
        return userRepository.findAllByLocationPermissionTrue().stream()
                .filter(User::isInHongdae)
                .map(UserDetailDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserLocation(long userId, double latitude, double longitude){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateLocation(latitude, longitude);
    }

}
