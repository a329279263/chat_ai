package cn.autumn.chat.controller;

import cn.autumn.chat.security.ShiroUtils2;
import cn.autumn.chat.service.ChatMessageService;
import cn.autumn.chat.service.RedissonService;
import cn.autumn.chat.service.UserService;
import cn.autumn.chat.thread.ThreadPoolService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/13 16:48
 * @version: 1.0
 */
@RestController
@AllArgsConstructor
@Data
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;


    private final SimpMessagingTemplate messagingTemplate;

    private final RedissonService redissonService;
    private final UserService userService;

    /**
     * 接收客户端消息
     * 广播消息给群聊
     */
    @MessageMapping("/sendMessage/{chatId}")
    public void sendMessageToGroup(@DestinationVariable Long chatId, String message, StompHeaderAccessor accessor) {
        final String openid = redissonService.getUserToken(accessor.getSessionId());
        ShiroUtils2.setUser(openid);
        ThreadPoolService.getInstance().submitTask(chatId + ":" + openid, openid, "ai生成", () -> {
            ShiroUtils2.setUser(openid);
            try {
                chatMessageService.sendMessageToGroup(chatId, message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
