package cn.autumn.chat.api;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/4/6 14:09
 * @version: 1.0
 */
public class AnswerContext {
    private boolean stopRequested = false;

    public synchronized void requestStop() {
        stopRequested = true;
    }

    public synchronized boolean isStopRequested() {
        return stopRequested;
    }
}
