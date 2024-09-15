package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.dto.NoticeResponse;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.entity.Notice;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.BlockRepository;
import project.atch.domain.user.repository.NoticeRepository;
import project.atch.domain.user.repository.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public List<UserDetailDto> getUsersDetail(long userId) {

        List<Long> blockedId = blockRepository.findBlockedIdsByBlockerId(userId);

        double latMin = 37.548264;
        double latMax = 37.564515;
        double lonMin = 126.914907;
        double lonMax = 126.928451;

        // 사용자 목록 조회
        List<UserDetailDto> users = userRepository.findUsersWithItems(latMin, latMax, lonMin, lonMax);

        // blockedId에 포함되지 않은 사용자만 필터링
        return users.stream()
                .filter(user -> !blockedId.contains(user.getUserId())) // blockedId에 없는 사용자만 필터링
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserLocation(long userId, double latitude, double longitude){
        User user = userRepository.findById(userId).orElseThrow();
        user.updateLocation(latitude, longitude);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotice(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_INFORMATION_NOT_FOUND));
        List<Notice> result = noticeRepository.findByUserOrderByCreatedAtDesc(user);
        return result.stream()
                .map(NoticeResponse::from)
                .toList();
    }
}
