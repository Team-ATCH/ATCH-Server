package project.atch.domain.chat.repository;

import project.atch.domain.chat.entity.Chat;
import reactor.core.publisher.Flux;

public interface ChatRepositoryCustom {
    Flux<Chat> findOldestMessagesFromAllRooms();
}
