package cn.autumn.chat.vo.req;

import com.zhipu.oapi.service.v4.model.ChatMessage;
import lombok.Data;

import java.util.List;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/12 10:53
 * @version: 1.0
 */
@Data
public class ChatMessageReq {
    private List<ChatMessage> messages;
}
