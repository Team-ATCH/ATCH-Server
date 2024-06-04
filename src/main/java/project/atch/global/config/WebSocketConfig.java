package project.atch.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import project.atch.global.stomp.StompPreHandler;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompPreHandler stompPreHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");//sub으로 시작되는 요청을 구독한 모든 사용자들에게 메시지를 broadcast함
        registry.setApplicationDestinationPrefixes("/pub");// pub로 시작되는 메시지는 message-handling methods로 라우팅됨
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/init") // 처음 webSocket에 접속할 때 HandShake와 통신을 담당할 엔드포인트
                .setAllowedOrigins("*"); // 출처는 모두 접근 가능
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompPreHandler); // StompHandler가 Websocket 앞단에서 token을 체크할 수 있도록 인터셉터로 설정
    }
}
