package cn.autumn.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.autumn.chat.constant.Constant.*;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/18 14:23
 * @version: 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class RedissonService {
    private final RedissonClient redissonClient;
    private final Map<String, RAtomicLong> requestCounters = new ConcurrentHashMap<>();

    public int incrementUserRequestCount(String openid) {
        RAtomicLong counter = requestCounters.computeIfAbsent(openid, key -> redissonClient.getAtomicLong(INCREMENT_USER_COUNT_PREFIX + key));

        // 自增计数器并获取自增后的值
        long count = counter.incrementAndGet();

        return (int) count;
    }

    public int incrementIpRequestCount(String ip) {
        RAtomicLong counter = requestCounters.computeIfAbsent(ip, key -> redissonClient.getAtomicLong(INCREMENT_IP_COUNT_PREFIX + key));

        // 自增计数器并获取自增后的值
        long count = counter.incrementAndGet();

        return (int) count;
    }


    public long incrementUserIdCount(String userId) {
        RAtomicLong counter = requestCounters.computeIfAbsent(userId, key -> redissonClient.getAtomicLong(USER_ID_SORT_COUNTER + key));
        // 自增计数器并获取自增后的值

        return counter.incrementAndGet();
    }

    public void clearCounters() {
        log.info("开始清理计数器....");
        // 遍历并清除所有计数器
        requestCounters.values().forEach(RAtomicLong::delete);
        requestCounters.clear();
    }


    /**
     * 保存WebSocket 的 sessionId 用户 和 token
     */
    public void saveUserSession(String sessionId, String token) {
        RMap<String, String> userSessionMap = redissonClient.getMap("userSessionMap");
        userSessionMap.put(sessionId, token);
    }

    /**
     * 根据sessionId获取当前用户token
     */
    public String getUserToken(String sessionId) {
        RMap<String, String> userSessionMap = redissonClient.getMap("userSessionMap");
        return userSessionMap.get(sessionId);
    }

    /**
     * 根据sessionId当前删除对应token
     */
    public void removeUserSession(String sessionId) {
        RMap<String, String> userSessionMap = redissonClient.getMap("userSessionMap");
        userSessionMap.remove(sessionId);
    }

}
