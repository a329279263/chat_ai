package cn.autumn.chat.service;

import cn.autumn.chat.api.AiModelApi;
import cn.autumn.chat.api.AnswerContext;
import cn.autumn.chat.security.ShiroUtils2;
import cn.autumn.chat.vo.resp.ChatMessageResp;
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
    private final RedissonService redissonService;
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

    public void sendMessageToGroup(String chatId, List<ChatMessage> list) {
        final long i = redissonService.incrementUserIdCount(ShiroUtils2.getUser());
        final ChatMessage last = list.getLast();
        sendUserMessage(chatId, (String) last.getContent(),i);
        processAiResponse(chatId,  list,i);
    }

    private void sendUserMessage(String chatId, String message, long msgId) {
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, ChatMessageRole.USER, message));
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, "", true, false));
    }

    private void processAiResponse(String chatId, List<ChatMessage> list, long msgId) {
        String key = ShiroUtils2.getUser() + msgId;
        StringBuffer aiResult = new StringBuffer();
        startAnswering(key);
        aiModelApi.callModelApi(list, resultMsg -> handleAiResult(chatId, msgId, resultMsg,aiResult),
                () -> handleAiCompletion(chatId, aiResult,key,msgId), () -> handleAiError(chatId, msgId), answerContexts.get(key));
    }

    private void handleAiResult(String chatId, Long msgId, String resultMsg, StringBuffer aiResult) {
        aiResult.append(resultMsg);
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, resultMsg, false));
    }

    private void handleAiCompletion(String chatId, StringBuffer aiResult, String key, Long msgId) {
        stopAnswering(key);
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, "", true));
        log.info("ai回答已完成");
    }

    private void handleAiError(String chatId, Long msgId) {
        messageService.sendOrderedMessage(new ChatMessageResp(chatId, msgId, AI_RESULT_PREFIX, true));
    }


}
