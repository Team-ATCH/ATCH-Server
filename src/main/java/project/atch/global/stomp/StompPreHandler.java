package project.atch.global.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import project.atch.domain.user.entity.OAuthProvider;
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

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor == null) return message;
        handleConnect(accessor);

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        User user = getUser(accessor);
        if (user != null){
            setSessionAttributes(accessor, user.getId(), user.getNickname());
            log.info("[StompPreHandler-preSend] connect: " + user.getId());
        }
    }

    private User getUser(StompHeaderAccessor accessor){
        User user = null;
        String jwtToken = accessor.getFirstNativeHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer")) {
            String token = jwtToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getUserEmail(token);
                String provider = jwtTokenProvider.getOAuthProvider(token);

                user = userRepository.findByEmailAndOAuthProvider(email, OAuthProvider.valueOf(provider)).orElseThrow();
            }
        }

        return user;
    }

    private void setSessionAttributes(StompHeaderAccessor accessor, Long userId, String nickname) {
        Map<String, Object> sessionAttributes = Objects.requireNonNull(accessor.getSessionAttributes());
        sessionAttributes.put("userNickname", nickname);
        sessionAttributes.put("userId", userId);
        accessor.setSessionAttributes(sessionAttributes);
    }
}