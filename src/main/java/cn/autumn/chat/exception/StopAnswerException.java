package cn.autumn.chat.exception;

import java.io.Serial;

/**
 * 停止回答异常
 */
public class StopAnswerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final String message;

    public StopAnswerException(String message) {
        this.message = message;
    }

    public StopAnswerException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
