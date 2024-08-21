package project.atch.domain.chat.repository;

import project.atch.domain.chat.entity.Chat;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatRepositoryCustom {
    Flux<Chat> findOldestMessagesFromAllRooms(int limit, long lastId, List<Long> excludedRoomIds);
}
