package cn.autumn.chat.service;

import cn.autumn.chat.domain.vo.resp.ChatMessageResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/21 16:14
 * @version: 1.0
 */
@Service
@Slf4j
public class MessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BlockingQueue<Runnable> sendQueue = new LinkedBlockingQueue<>();

    public MessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        // 启动一个线程来发送队列中的消息
        new Thread(() -> {
            while (true) {
                try {
                    Runnable task = sendQueue.take(); // 等待并取出消息
                    task.run(); // 发送消息
                } catch (InterruptedException e) {
                    log.error("线程被中断", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public void sendOrderedMessage(ChatMessageResp result) {
        //将发送消息任务放入队列
        sendQueue.add(() -> {
            // 打印出来消息队列就正常，不打印日志则乱序？
//            log.info("广播消息：【" + result.getContent() + "】");
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            messagingTemplate.convertAndSend("/topic/" + result.getChatId(), result);
        });
    }
}
