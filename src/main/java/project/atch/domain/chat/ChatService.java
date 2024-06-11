package project.atch.domain.chat;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public Mono<Chat> saveChatMessage(Long roomId, String content, Long fromUserId) {
        return chatRepository.save(
                new Chat(roomId, content, fromUserId, new Date()));
    }

    @Transactional
    public Flux<ResponseMessageDto> findChatMessages(Long id) {
        Flux<Chat> chatMessages = chatRepository.findAllByRoomId(id);
        return chatMessages.map(ResponseMessageDto::of);
    }
}
