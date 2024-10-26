package project.atch.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.notice.entity.Notice;
import project.atch.domain.notice.repository.NoticeRepository;
import project.atch.domain.user.entity.Item;
import project.atch.domain.user.entity.ItemName;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.entity.UserItem;
import project.atch.domain.user.repository.ItemRepository;
import project.atch.domain.user.repository.UserItemRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;

    @Transactional
    public void createItemNotice(User user, ItemName itemName){
        // 사용자에게 아이템 부여
        Item item = itemRepository.findById(itemName.getValue()).orElseThrow();
        UserItem userItem = new UserItem(user, item);
        userItemRepository.save(userItem);

        // 공지 저장
        Notice notice = Notice.of(itemName, user);
        noticeRepository.save(notice);
    }

}
