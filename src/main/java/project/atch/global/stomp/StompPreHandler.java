package project.atch.global.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.UserRepository;
import project.atch.global.jwt.JwtTokenProvider;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompPreHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoomUserCountManager roomUserCountManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor == null) return message;

        switch (accessor.getCommand()){
            case CONNECT:
                // 클라이언트의 초기 연결 요청을 확인하고, 헤더의 인증 정보를 검토하여 연결을 수락
                handleConnect(accessor);
                break;
            case SUBSCRIBE:
                // 해당 주제나 방을 구독하려는 요청 처리
                handleSubscribe(accessor);
                break;
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String email = getEmail(accessor);
        if (email != null) {
            User user = userRepository.findByEmail(email).orElseThrow();
            setSessionAttributes(accessor, user.getId(), user.getNickname());
            log.info("[StompPreHandler-preSend] connect: " + user.getId());
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getFirstNativeHeader("destination");
        if (destination != null) {
            long roomId = Long.parseLong(destination.substring(destination.lastIndexOf('/') + 1));
            roomUserCountManager.incrementUserCount(roomId);
            log.info("[StompPreHandler-preSend] subscribe: {}번방에 {}명", roomId, roomUserCountManager.getUserCount(roomId));
        }
    }

    private String getEmail(StompHeaderAccessor accessor){
        String email = null;
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer")) {
            String token = jwtToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                email = jwtTokenProvider.getUserEmail(token);
            }
        }

        return email;
    }

    private void setSessionAttributes(StompHeaderAccessor accessor, Long userId, String nickname) {
        Map<String, Object> sessionAttributes = Objects.requireNonNull(accessor.getSessionAttributes());
        sessionAttributes.put("userNickname", nickname);
        sessionAttributes.put("userId", userId);
        accessor.setSessionAttributes(sessionAttributes);
    }
}