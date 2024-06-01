package cn.autumn.chat.controller;

import cn.autumn.chat.service.SSELocalChatMessageService;
import cn.autumn.chat.util.SSEUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/4/28 11:00
 * @version: 1.0
 */
@RestController
@AllArgsConstructor
@Data
@Slf4j
@RequestMapping("/sse")
public class TestController {


    private final SSELocalChatMessageService messageService;

    @RequestMapping(value = "/sendMessage", method = RequestMethod.GET, produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribe(HttpServletRequest request) {
//        System.out.println("2342423,id=" + JSONUtil.toJsonStr(body));
        // 添加订阅,建立sse链接
        final String subId = request.getParameter("subId");
        SseEmitter emitter = SSEUtils.addSub(subId);
        new Thread(() -> {
            try {
                for (int i = 0; i < 60; i++) {
                    // 发送消息
                    SSEUtils.pubMsg(subId, "", String.valueOf(i), subId + " - hmg come " + i);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                // 消息发送完关闭订阅
                SSEUtils.closeSub(subId);
            }
        }).start();
        return emitter;
    }


    /**
     * 停止回答
     */
    @GetMapping("/sendMessage/{msgId}")
    public void stopAnswering(@PathVariable String msgId) {
//        chatMessageService.stopAnswering(ShiroUtils2.getUser() + msgId);
    }


}
