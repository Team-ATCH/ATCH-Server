package project.atch.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.user.dto.UserDetailDto;
import project.atch.domain.user.service.HomeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home")
    public List<UserDetailDto> getUsersDetail(){
        return homeService.getUsersDetail();
    }
}
