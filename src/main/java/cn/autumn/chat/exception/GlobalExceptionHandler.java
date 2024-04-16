package cn.autumn.chat.exception;

import cn.autumn.chat.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public R<Void> handleException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return R.fail("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 没有权限
     */
    @ExceptionHandler({AccessDeniedException.class})
    public R<Void> handleAccessDeniedException(AccessDeniedException e) {
        return R.fail("很抱歉，您没有权限访问，请联系管理员");
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> notFount(RuntimeException e) {
        log.error("运行时异常:", e);
        return R.fail("运行时异常:" + e.getMessage());
    }


    /**
     * 身份信息已过期
     */
    @ExceptionHandler(AuthenticationException.class)
    public R<Void> authenticationException(AuthenticationException e) {
        log.error("身份验证失败:", e);
        return R.fail(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> businessHandle(BusinessException e) {
        log.error("业务异常:" + e.getMessage());
        return R.fail(e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public R<Void> NullPointerHandle(NullPointerException e) {
        log.error("空指针异常:", e);
        return R.fail("必填数据存在空值，请联系管理员");
    }
}
