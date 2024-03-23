package cn.autumn.chat.job;

import cn.autumn.chat.service.RedissonService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/21 18:37
 * @version: 1.0
 */
@Component
@AllArgsConstructor
public class ClearJob {


    private final RedissonService redissonService;

    /**
     * 清理计数器，每次重启即刻运行
     */
    @Scheduled(fixedRate = 1000 * 60 * 30) // 半小时一次
    public void clearCounters() {
        redissonService.clearCounters();
    }

}
