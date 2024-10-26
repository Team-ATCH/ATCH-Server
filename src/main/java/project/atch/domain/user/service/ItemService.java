package project.atch.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.entity.ItemName;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.entity.UserItem;
import project.atch.domain.user.repository.ItemRepository;
import project.atch.domain.user.repository.UserItemRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.fcm.FCMPushRequestDto;
import project.atch.global.fcm.FCMService;

import java.io.IOException;

/**
 * 알림용 itemSerivce
 * 지금 사용 안함
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final FCMService fcmService;

    public void giveWelcomeItemPerUser(User user){
        String itemName;
        if (user.getId() % 2 == 0){
            UserItem userItem = UserItem.builder()
                    .item(itemRepository.findById(ItemName.ONLINE.getValue()).get())
                    .user(user)
                    .build();
            userItemRepository.save(userItem);
            itemName = ItemName.ONLINE.getName();
        } else {
            UserItem userItem = UserItem.builder()
                    .item(itemRepository.findById(ItemName.LOVELY.getValue()).get())
                    .user(user)
                    .build();
            userItemRepository.save(userItem);
            itemName = ItemName.LOVELY.getName();
        }

        FCMPushRequestDto dto = FCMPushRequestDto.makeItemAlarm(user.getFcmToken(), itemName, itemName + "이 도착했습니다!");
        try {
            fcmService.pushAlarm(dto);  // 동기적으로 FCM 알람 전송
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FCM_SERVER_COMMUNICATION_FAILED);
        }
    }

    public void giveItem(User user, ItemName itemNumber){
        UserItem userItem = UserItem.builder()
                .item(itemRepository.findById(itemNumber.getValue()).get())
                .user(user)
                .build();
        userItemRepository.save(userItem);
        String itemName = itemNumber.getName();

        FCMPushRequestDto dto = FCMPushRequestDto.makeItemAlarm(user.getFcmToken(), itemName, itemName + "이 도착했습니다!");
        try {
            fcmService.pushAlarm(dto);  // 동기적으로 FCM 알람 전송
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FCM_SERVER_COMMUNICATION_FAILED);
        }
    }


}
