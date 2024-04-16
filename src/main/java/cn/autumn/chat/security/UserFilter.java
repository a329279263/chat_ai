package cn.autumn.chat.security;

import cn.autumn.chat.constant.Constant;
import cn.autumn.chat.util.PathMathUtil;
import cn.autumn.chat.vo.R;
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
public class UserFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("进入 BrandFilter");
        String openid = request.getHeader(Constant.TOKEN_HEAD);
        if (openid == null || !openid.startsWith(Constant.TOKEN_PREFIX)) {
            log.info("身份验证失败，请求路径：" + request.getRequestURI());
            R.responseOutDiy(response, R.fail("身份信息已过期，请重新登录"));
            return;
        }
        ShiroUtils2.setUser(openid.substring(Constant.TOKEN_PREFIX.length()));
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PathMathUtil.whiteListMatch(request);
    }
}
