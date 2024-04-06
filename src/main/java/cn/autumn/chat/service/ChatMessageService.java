package cn.autumn.chat.service;

import cn.autumn.chat.api.AiModelApi;
import cn.autumn.chat.api.AnswerContext;
import cn.autumn.chat.domain.Chat;
import cn.autumn.chat.domain.ChatMessageRecord;
import cn.autumn.chat.domain.vo.resp.ChatMessageResp;
import cn.autumn.chat.security.ShiroUtils2;
import cn.hutool.core.util.StrUtil;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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


    private static final int HISTORY_MESSAGE_LIMIT = 10;
    private static final ChatMessageRole ASSISTANT_ROLE = ChatMessageRole.ASSISTANT;
    private static final String AI_RESULT_PREFIX = "An error occurred";


    private final AiModelApi aiModelApi;
    private final ChatMessageRecordService messageRecordService;
    private final UserService userService;
    private final MessageService messageService;
    private final ConcurrentHashMap<String, AnswerContext> answerContexts = new ConcurrentHashMap<>();

    public void startAnswering(String requestId) {
        answerContexts.put(requestId, new AnswerContext());
    }

    /**
     * 前端手动调用/完成回答后程序调用
     */
    public void stopAnswering(String requestId) {
        Optional.ofNullable(answerContexts.remove(requestId))
                .ifPresent(AnswerContext::requestStop);
    }

    public void sendMessageToGroup(Long chatId, String message) throws InterruptedException {
        final ChatMessageRecord userMsg = messageRecordService.createNewMessage(chatId, message);
        sendUserMessage(chatId, userMsg);
        processAiResponse(chatId, userMsg);
    }

    private void sendUserMessage(Long chatId, ChatMessageRecord userMsg) {
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, userMsg.getId(), ChatMessageRole.USER, userMsg.getContent()));
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, userMsg.getId(), "", true, false));

    }

    private void processAiResponse(Long chatId, ChatMessageRecord userMsg) {
        String key = userMsg.getOpenid() + userMsg.getId();
        Long msgId = userMsg.getId();
        StringBuffer aiResult = new StringBuffer();
        startAnswering(key);
        List<ChatMessage> historyMessage = getHistoryMessage(userMsg.getChat());
        aiModelApi.callModelApi(historyMessage, resultMsg -> handleAiResult(chatId, msgId, resultMsg,aiResult),
                () -> handleAiCompletion(chatId, aiResult,key,msgId), () -> handleAiError(chatId, msgId), answerContexts.get(key));
    }

    private List<ChatMessage> getHistoryMessage(Chat chat) {
        return chat.getHistoryMessage(HISTORY_MESSAGE_LIMIT);
    }

    private void handleAiResult(Long chatId, Long msgId, String resultMsg, StringBuffer aiResult) {
        aiResult.append(resultMsg);
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, resultMsg, false));
    }

    private void handleAiCompletion(Long chatId, StringBuffer aiResult, String key, Long msgId) {
        saveAiMessageResponse(aiResult.toString(),msgId);
        stopAnswering(key);
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, "", true));
        log.info("ai回答已完成");
    }

    private void handleAiError(Long chatId, Long msgId) {
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, AI_RESULT_PREFIX, true));
    }

    private void saveAiMessageResponse(String aiResult, Long msgId) {
        final String openid = ShiroUtils2.getUser();
        if (StrUtil.isNotBlank(aiResult)) {
            messageRecordService.getByAnswerId(msgId).ifPresent(record -> {
                record.setContent(aiResult);
                record.update();
            });
        }
        userService.qaCountSub(openid);
    }

}
