package cn.autumn.chat.config;

import cn.autumn.chat.constant.Constant;
import cn.autumn.chat.service.RedissonService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/15 11:08
 * @version: 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private RedissonService redissonService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/chat");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //一个pc用，一个小程序用 // 根据需要配置允许的源-必须！
        registry.addEndpoint("/pc/ws")
                .setAllowedOrigins("https://dev.xcall.cn", "https://lzh329279263.cn/")
                .withSockJS();
        registry.addEndpoint("/miniapp/ws");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.taskExecutor().corePoolSize(1).maxPoolSize(1);
        registration.interceptors(new WebSocketInterceptor(redissonService));
    }

    private record WebSocketInterceptor(RedissonService redissonService) implements ChannelInterceptor {

        @SneakyThrows
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            // 连接和关闭的请求需要在这里处理，在发送之前如果没有验证成功则抛出异常。
            // 客户端调用信息不可在此监听，ShiroUtils2 ThreadLocal 是记录在 StompHeaderAccessor 的线程中，和 mvc 不是同一个
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                // 在连接建立时进行身份验证等逻辑处理
                String token = accessor.getFirstNativeHeader(Constant.TOKEN_HEAD);
                if (token == null || !token.startsWith(Constant.TOKEN_PREFIX)) {
                    log.error("WebSocketConfig，身份信息已过期，请重新登录");
                    throw new AuthenticationException("身份信息已过期，请重新登录");
                }
                String openid = token.substring(Constant.TOKEN_PREFIX.length());
                redissonService.saveUserSession(accessor.getSessionId(), openid);
            }
            if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                redissonService.removeUserSession(accessor.getSessionId());
            }
            return message;
        }
    }
}
