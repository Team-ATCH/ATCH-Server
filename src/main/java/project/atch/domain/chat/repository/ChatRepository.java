package project.atch.domain.chat.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import project.atch.domain.chat.entity.Chat;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String>, ChatRepositoryCustom {
    Flux<Chat> findAllByRoomId(Long roomId);

}



