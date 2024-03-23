package cn.autumn.chat.security;

import cn.autumn.chat.constant.Constant;
import cn.autumn.chat.domain.vo.R;
import cn.autumn.chat.service.RedissonService;
import cn.autumn.chat.util.IpUtil;
import cn.autumn.chat.util.PathMathUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestCounterFilter extends OncePerRequestFilter {

    private final RedissonService redissonService;

    public RequestCounterFilter(RedissonService redissonService) {
        this.redissonService = redissonService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("进入 RequestCounterFilter");
        // 获取用户ID，可以通过从请求参数、请求头或会话中获取
        String openid = ShiroUtils2.getUser();
        if (StrUtil.isEmpty(openid)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 调用用户请求计数器服务
        final int userRequestCount = redissonService.incrementUserRequestCount(openid);
        // 调用Ip请求计数器服务
        final String clientIP = IpUtil.getIpAddr(request);
        final int ipRequestCount = redissonService.incrementIpRequestCount(clientIP);
        if (userRequestCount > Constant.MAX_USER_REQUEST_COUNT || ipRequestCount > Constant.MAX_IP_REQUEST_COUNT) {
            log.info("用户请求计数超过最大值，用户ID：" + openid + "，userRequestCount:" + userRequestCount + "，ipRequestCount" + ipRequestCount);
            R.responseOutDiy(response, R.fail("请求过于频繁，请稍后再试。"));
            return;
        }
        // 继续调用下一个过滤器或处理请求
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PathMathUtil.whiteListMatch(request);
    }
}
