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

    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersDetail() {

        double latMin = 37.546856;
        double latMax = 37.566418;
        double lonMin = 126.907221;
        double lonMax = 126.933994;

        return userRepository.findUsersWithItems(latMin, latMax, lonMin, lonMax);
    }

    @Transactional
    public void updateUserLocation(long userId, double latitude, double longitude){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateLocation(latitude, longitude);
    }

}
