package cn.autumn.chat.api;

import cn.autumn.chat.exception.StopAnswerException;
import cn.autumn.chat.properties.ZhiPuAiProperties;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/13 16:55
 * @version: 1.0
 */
@Service
@Slf4j
public class AiModelApi {

    @Autowired
    private ZhiPuAiProperties aiConfig;

    private ClientV4 client;

    // 请自定义自己的业务id
    private final static String requestIdTemplate = "mycompany-%d";

    private final static ObjectMapper mapper = defaultObjectMapper();


    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.addMixIn(ChatFunction.class, ChatFunctionMixIn.class);
        mapper.addMixIn(ChatCompletionRequest.class, ChatCompletionRequestMixIn.class);
        mapper.addMixIn(ChatFunctionCall.class, ChatFunctionCallMixIn.class);
        return mapper;
    }

    public static Flowable<ChatMessageAccumulator> mapStreamToAccumulator(Flowable<ModelData> flowable) {
        return flowable.map(chunk -> new ChatMessageAccumulator(chunk.getChoices().getFirst().getDelta(), null, chunk.getChoices().getFirst(), chunk.getUsage(), chunk.getCreated(), chunk.getId()));
    }


    @PostConstruct
    public void init() {
        client = new ClientV4.Builder(aiConfig.getApiKey(), aiConfig.getApiSecret()).build();
    }

    // 模拟AiModelApi.chatMessage方法
    public static void chatMessage(List<ChatMessage> message, Consumer<String> consumer, Runnable onComplete) {
        // 模拟生成消息的过程，每隔1秒生成一个字并发送给前端
        try {
            String text = "Hello, this is a test message.";
            for (int i = 0; i < 5; i++) {
                try {
                    consumer.accept(String.valueOf(text.charAt(i)));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            onComplete.run();
        } catch (Exception e) {
            onComplete.run();
        }
    }

    public void callModelApi(List<ChatMessage> messages, Consumer<String> consumer, Runnable complete, Runnable errHandle, AnswerContext answerContext) {
        try {
            chatMessage2(messages, consumer, complete,answerContext);
        } catch (StopAnswerException e) {
            complete.run();
        } catch (Exception e) {
            errHandle.run();
            log.error("callModelApi error：", e);
        }
    }


    public void chatMessage2(List<ChatMessage> messages, Consumer<String> consumer, Runnable complete, AnswerContext answerContext) {
        // 函数调用参数构建部分
        final ChatCompletionRequest chatCompletionRequest = buildChatRequest(messages);
        ModelApiResponse modelApiResponse = client.invokeModelApi(chatCompletionRequest);
        if (modelApiResponse.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            final ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(modelApiResponse.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (answerContext.isStopRequested()) {
                                throw new StopAnswerException("回答已终止.");
                            }
                            if (isFirst.getAndSet(false)) {
                                System.out.print("Response: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                System.out.print(accumulator.getDelta().getContent());
                                consumer.accept(accumulator.getDelta().getContent());
                            }
                        }
                    })
                    .doOnComplete(complete::run)
                    .lastElement()
                    .blockingGet();
            modelApiResponse.setFlowable(null);
            modelApiResponse.setData(getModelData(chatMessageAccumulator, chatCompletionRequest));
        }
        log.info("model output:" + JSONUtil.toJsonStr(modelApiResponse));
    }

    @NotNull
    private static ModelData getModelData(ChatMessageAccumulator chatMessageAccumulator, ChatCompletionRequest chatCompletionRequest) {
        Choice choice = new Choice(chatMessageAccumulator.getChoice().getFinishReason(), 0L, chatMessageAccumulator.getDelta());
        List<Choice> choices = new ArrayList<>();
        choices.add(choice);
        ModelData data = new ModelData();
        data.setChoices(choices);
        data.setUsage(chatMessageAccumulator.getUsage());
        data.setId(chatMessageAccumulator.getId());
        data.setCreated(chatMessageAccumulator.getCreated());
        data.setRequestId(chatCompletionRequest.getRequestId());
        return data;
    }

    private String createRequestId() {
        return String.format(requestIdTemplate, System.currentTimeMillis());
    }

    private ChatCompletionRequest buildChatRequest(List<ChatMessage> messages) {
        return ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(createRequestId())
                .toolChoice("auto")
                .build();
    }
}
