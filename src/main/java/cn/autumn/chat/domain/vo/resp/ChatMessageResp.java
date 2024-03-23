package cn.autumn.chat.domain.vo.resp;

import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/15 16:33
 * @version: 1.0
 */
@Data
@NoArgsConstructor
public class ChatMessageResp {
    /**
     * 会话/分组id
     */
    private Long chatId;

    /**
     * 消息id，只有用户发送才有
     */
    private Long msgId;

    /**
     * 回复消息id，只有ai回答才有
     */
    private Long answerMsgId;

    /**
     * user/assistant
     * 使用 ChatMessageRole
     */
    private String role;

    private String content;

    /**
     * 该消息是否初始化
     */
    private Boolean initialized = false;

    /**
     * 该消息是否已经全部说完
     */
    private Boolean completed = false;


    public ChatMessageResp(Long chatId, Long msgId, ChatMessageRole role, String content) {
        this.chatId = chatId;
        if (role == ChatMessageRole.USER) {
            this.msgId = msgId;
        } else {
            this.answerMsgId = msgId;
        }
        this.role = role.value();
        this.content = content;
        this.completed = true;
    }

    public ChatMessageResp(Long chatId, Long answerMsgId, String content, Boolean completed) {
        this.chatId = chatId;
        this.answerMsgId = answerMsgId;
        this.role = ChatMessageRole.ASSISTANT.value();
        this.content = content;
        this.completed = completed;
    }

    public ChatMessageResp(Long chatId, Long answerMsgId, String content, Boolean initialized, Boolean completed) {
        this.chatId = chatId;
        this.answerMsgId = answerMsgId;
        this.role = ChatMessageRole.ASSISTANT.value();
        this.content = content;
        this.initialized = initialized;
        this.completed = completed;
    }
}
