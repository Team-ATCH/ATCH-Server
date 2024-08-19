package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersDetail() {

        double latMin = 37.548264;
        double latMax = 37.564515;
        double lonMin = 126.914907;
        double lonMax = 126.928451;

        return userRepository.findUsersWithItems(latMin, latMax, lonMin, lonMax);
    }

    @Transactional
    public void updateUserLocation(long userId, double latitude, double longitude){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateLocation(latitude, longitude);
    }

}
