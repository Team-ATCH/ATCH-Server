package project.atch.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.user.dto.RequestLocationDto;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.service.HomeService;
import project.atch.global.security.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home")
    public List<UserDetailDto> getUsersDetail(){
        return homeService.getUsersDetail();
    }

    @PutMapping("/home/locate")
    public void updateUserLocation(@RequestBody RequestLocationDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        homeService.updateUserLocation(userDetails.getUserId(), dto.getLatitude(), dto.getLongitude());
    }
}
