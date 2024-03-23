package cn.autumn.chat.thread;

import cn.autumn.chat.security.ShiroUtils2;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/03/14 16:43
 * @version: 1.0
 */
@Slf4j
public class ThreadPoolService {
    private static ThreadPoolService instance;
    private final ThreadPoolExecutor threadPool;
    private final Set<String> submittedTasks;

    private ThreadPoolService() {
        // 初始化线程池
        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepAliveTime = 60;
        int queueSize = 100;
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize));
        //线程安全
        submittedTasks = Collections.synchronizedSet(new HashSet<>());
    }

    public synchronized static ThreadPoolService getInstance() {
        if (instance == null) {
            instance = new ThreadPoolService();
        }
        return instance;
    }


    /**
     * 防止任务重复提交、并发处理同一个任务
     */
    public void submitTask(String seq, String token, String msg, Runnable task) {
        synchronized (submittedTasks) {
            if (!submittedTasks.contains(seq)) {
                threadPool.submit(() -> {
                    try {
                        log.info("{} start seq: {}", msg, seq);
                        if (token != null) {
                            ShiroUtils2.setUser(token);
                        }
                        task.run();
                    } catch (Exception t) {
                        log.error("{} exception：{}", msg, seq, t);
                    } finally {
                        log.info("{} complete seq: {}", msg, seq);
                        submittedTasks.remove(seq); // 任务执行完成后清除任务标识
                        if (token != null) {
                            ShiroUtils2.cleanUser();
                        }
                    }
                });
                submittedTasks.add(seq);
            }
        }
    }

    public void shutdownThreadPool() {
        threadPool.shutdown();
    }
}
