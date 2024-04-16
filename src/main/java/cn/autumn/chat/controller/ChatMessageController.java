package cn.autumn.chat.controller;

import cn.autumn.chat.security.ShiroUtils2;
import cn.autumn.chat.service.ChatMessageService;
import cn.autumn.chat.service.RedissonService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 接收客户端消息
     * 广播消息给群聊
     */
    @MessageMapping("/sendMessage/{chatId}")
    public void sendMessageToGroup(@DestinationVariable String chatId, String message, StompHeaderAccessor accessor) {
        if (StrUtil.isBlank(message))return;
        final List<ChatMessage> list = JSONUtil.toList(message, ChatMessage.class);
        getUserId(accessor);
        chatMessageService.sendMessageToGroup(chatId, list);
    }

    /**
     * 停止回答
     */
    @MessageMapping("/stopAnswering/{msgId}")
    public void stopAnswering(@DestinationVariable String msgId, StompHeaderAccessor accessor) {
        chatMessageService.stopAnswering(getUserId(accessor)+msgId);
    }

    private String getUserId(StompHeaderAccessor accessor) {
        final String openid = redissonService.getUserToken(accessor.getSessionId());
        ShiroUtils2.setUser(openid);
        return openid;
    }

}
