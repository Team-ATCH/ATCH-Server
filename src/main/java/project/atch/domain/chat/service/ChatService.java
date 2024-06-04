package project.atch.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.chat.entity.Chat;
import project.atch.domain.chat.dto.ChatDto;
import project.atch.domain.chat.repository.ChatRepository;
import project.atch.domain.room.entity.Room;
import project.atch.domain.room.repository.RoomRepository;
import project.atch.domain.user.User;
import project.atch.domain.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @Transactional
    public void createAndSendChat(Long roomId, String message, Long userId){
        createChat(roomId, message, userId);
        sendChat(roomId, message, userId);
    }

    @Transactional
    public void createChat(Long roomId, String message, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        Room room = roomRepository.findById(roomId).orElseThrow();
        Chat chat = Chat.builder()
                .room(room)
                .message(message)
                .user(user)
                .sendAt(LocalDateTime.now())
                .build();
        chatRepository.save(chat);
    }

    private void sendChat(Long roomId, String message, Long userId){
        simpMessageSendingOperations.convertAndSend("/sub/room/" + roomId, message);
        log.info("sending message from {} to /sub/room/{}", userId, roomId);
    }

    public List<ChatDto> findAllChatByRoomId(Long roomId) {
        List<Chat> chatHistory = chatRepository.findAllByRoomId(roomId);
        List<ChatDto> chatDtoList = new ArrayList<>();

        for (Chat chat : chatHistory) {
            ChatDto chatDto = ChatDto.builder()
                    .message(chat.getMessage())
                    .sender(chat.getUser().getNickname())
                    .sendAt(chat.getSendAt())
                    .build();
            chatDtoList.add(chatDto);
        }
        return chatDtoList;
    }

}
