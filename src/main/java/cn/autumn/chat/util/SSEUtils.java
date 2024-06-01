package cn.autumn.chat.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

@Slf4j
public class SSEUtils {
    // timeout
    private static Long DEFAULT_TIME_OUT = 5 * 60 * 1000L;

    // 为每个组和用户存储 SseEmitters
    private static final Map<String, Map<String, SseEmitter>> subscribeMap = new ConcurrentHashMap<>();

    /**
     * 为组中的用户添加订阅。
     */
    public static SseEmitter addSub(String groupId, String userId) {
        if (null == groupId || null == userId || groupId.isEmpty() || userId.isEmpty()) {
            return null;
        }

        Map<String, SseEmitter> userSubscriptions = subscribeMap.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>());
        SseEmitter emitter = userSubscriptions.get(userId);
        if (null == emitter) {
            emitter = new SseEmitter(DEFAULT_TIME_OUT);

            emitter.onTimeout(() -> {
                log.info("onTimeout,groupId=" + groupId + ",userId=" + userId);
                closeSub(groupId, userId);
            });

            emitter.onCompletion(() -> {
                log.info("onCompletion,groupId=" + groupId + ",userId=" + userId);
                closeSub(groupId, userId);
            });

            userSubscriptions.put(userId, emitter);
        }
        return emitter;
    }

    /**
     * 向组中的所有用户发布消息。
     */
    public static void pubMsgToGroup(String groupId, String name, String id, Object msg) {
        Map<String, SseEmitter> userSubscriptions = subscribeMap.get(groupId);
        if (userSubscriptions != null) {
            userSubscriptions.forEach((userId, emitter) -> {
                try {
                    emitter.send(event().name(name).id(id).data(msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 关闭组中用户的订阅
    public static void closeSub(String groupId, String... userIds) {
        Map<String, SseEmitter> userSubscriptions = subscribeMap.get(groupId);
        if (userSubscriptions == null) {
            // 如果该组没有任何订阅，直接返回
            return;
        }

        if (userIds == null || userIds.length == 0) {
            // 如果没有提供特定的 userIDs，关闭并删除整个组下的所有订阅
            for (Map.Entry<String, SseEmitter> entry : userSubscriptions.entrySet()) {
                entry.getValue().complete();
            }
            // 删除整个组
            subscribeMap.remove(groupId);
        } else {
            // 如果提供了特定的 userIDs，只关闭这些用户的订阅
            for (String userId : userIds) {
                SseEmitter emitter = userSubscriptions.get(userId);
                if (emitter != null) {
                    emitter.complete();
                    userSubscriptions.remove(userId);
                }
            }
            // 如果组中没有更多的用户，删除整个组
            if (userSubscriptions.isEmpty()) {
                subscribeMap.remove(groupId);
            }
        }
    }

}
