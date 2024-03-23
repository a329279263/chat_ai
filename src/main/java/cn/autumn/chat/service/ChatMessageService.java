package cn.autumn.chat.service;

import cn.autumn.chat.api.AiModelApi;
import cn.autumn.chat.domain.Chat;
import cn.autumn.chat.domain.ChatMessageRecord;
import cn.autumn.chat.domain.vo.resp.ChatMessageResp;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import io.ebean.DB;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/11 17:31
 * @version: 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class ChatMessageService {

    private final AiModelApi aiModelApi;

    private final ChatMessageRecordService messageRecordService;
    private final UserService userService;
    private final MessageService messageService;


    /**
     * ai对话，记录双方聊天记录
     */
    public void sendMessageToGroup(Long chatId, String message) throws InterruptedException {
        final ChatMessageRecord userMsg = messageRecordService.createNewMessage(chatId, message);
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, userMsg.getId(), ChatMessageRole.USER, message));
        Thread.sleep(30);
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, userMsg.getId(), "", true, false));
        chatMessage(userMsg);
    }

    public void chatMessage(ChatMessageRecord record) {
        final Long msgId = record.getId();
        final String openid = record.getOpenid();
        final Chat chat = record.getChat();
        final Long chatId = chat.getId();
        final List<ChatMessage> historyMessage = chat.getHistoryMessage();
        historyMessage.add(new ChatMessage(ChatMessageRole.USER.value(), record.getContent()));
        final long l = System.currentTimeMillis();
        StringBuffer aiResult = new StringBuffer();
        aiModelApi.callModelApi(historyMessage, resultMsg -> {
            aiResult.append(resultMsg);
            messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, resultMsg, false));
        }, () -> {
            DB.save(new ChatMessageRecord(chat, ChatMessageRole.ASSISTANT, aiResult.toString()));
            userService.qaCountSub(openid);
            messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, "", true));
            log.info("ai回答已完成，耗时:{}", System.currentTimeMillis() - l);
        });
    }

}
