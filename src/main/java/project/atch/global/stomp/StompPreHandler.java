package project.atch.global.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
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

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    // token에서 user를 식별해 session에 userid, nickname을 넣는다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor != null && StompCommand.CONNECT == accessor.getCommand()) {
            String email = getEmail(accessor);
            if (email != null){
                User user = userRepository.findByEmail(email).orElseThrow();
                setSessionAttributes(accessor, user.getId(), user.getNickname());

                log.info("[StomptPreHandler-preSend] connect: " + user.getId());
                return message;
            }
        }

        return message;
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