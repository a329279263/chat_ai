package cn.autumn.chat.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/4/28 11:00
 * @version: 1.0
 */
@Service
public class SSEService {

    private static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String sseClientId) {
        if (StringUtils.isEmpty(sseClientId)) {
            return null;
        }
        SseEmitter sseEmitter;
        SseEmitter getClientEmitter = sseCache.get(sseClientId);
        if (getClientEmitter == null || !checkSseConnectAlive(getClientEmitter)) {
            // 生存时间设置 默认30s
            sseEmitter = new SseEmitter(0L);
            // 设置前端的重试时间
            try {
                sseEmitter.send(SseEmitter.event().reconnectTime(30L).data("连接成功"));
                sseCache.put(sseClientId, sseEmitter);
                System.out.println("add " + sseClientId);
                sseEmitter.onTimeout(() -> {
                    System.out.println(sseClientId + "超时");
                    sseCache.remove(sseClientId);
                });
                sseEmitter.onCompletion(() -> System.out.println("已关闭连接：" + sseClientId));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("已存在！");
            sseEmitter = getClientEmitter;
        }

        return sseEmitter;
    }


    public boolean sendOneClientMessage(String sseClientId, Object msg) throws IOException {
        SseEmitter sseEmitter = sseCache.get(sseClientId);
        if (sseEmitter != null && checkSseConnectAlive(sseEmitter)) {
            sseEmitter.send(SseEmitter.event().data(msg));
            return true;
        } else {
            return false;
        }
    }

    public void sendAllClientMsg(Object msg) {
        if (sseCache != null && !sseCache.isEmpty()) {
            sseCache.forEach((sseClientId, sseEmitter) -> {
                if (sseEmitter != null) {
                    if (checkSseConnectAlive(sseEmitter)) {
                        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter
                                .event()
                                .data(msg)
                                .reconnectTime(30L);
                        try {
                            sseEmitter.send(sseEventBuilder);
                        } catch (Exception e) {
                            System.err.println("SSE check sseClientId send error:" + sseClientId);
                        }
                    } else {
                        sseEmitter.complete();
                        sseCache.remove(sseClientId);
                        System.out.println("SSE check sseClientId offline:" + sseClientId);
                    }
                }
            });
        }
    }

    public static Object getFieldInstance(Object obj, String fieldPath) {
        String fields[] = fieldPath.split("#");
        for (String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if (obj == null) {
                return null;
            }
        }

        return obj;
    }

    public static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field;
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
            }
        }

        return null;
    }

    public boolean close(String sseClientId) {
        SseEmitter sseEmitter = sseCache.get(sseClientId);
        if (sseEmitter != null) {
            System.out.println("SSE active close connection :" + sseClientId);
            sseEmitter.complete();
            sseCache.remove(sseClientId);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkSseConnectAlive(SseEmitter sseEmitter) {
        if (sseEmitter == null) {
            return false;
        }
        return !(Boolean) getFieldInstance(sseEmitter, "sendFailed") &&
                !(Boolean) getFieldInstance(sseEmitter, "complete");
    }


    public void aliveCheck(ApplicationArguments args) throws Exception {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendAllClientMsg("Check Alive");
            }
        }, 3 * 1000, 3 * 1000);
    }


}
