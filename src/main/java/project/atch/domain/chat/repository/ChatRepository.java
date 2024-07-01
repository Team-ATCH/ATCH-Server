package project.atch.domain.chat.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import project.atch.domain.chat.entity.Chat;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String>, ChatRepositoryCustom {
    Flux<Chat> findAllByRoomId(Long roomId, Sort sort);

    default Flux<Chat> findAllByRoomIdOrderByCreatedAtDesc(Long roomId) {
        return findAllByRoomId(roomId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}