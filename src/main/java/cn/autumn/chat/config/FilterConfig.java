package cn.autumn.chat.config;

import cn.autumn.chat.security.RequestCounterFilter;
import cn.autumn.chat.security.UserFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/18 13:58
 * @version: 1.0
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterChainProxy filterChainProxy(UserFilter userFilter, RequestCounterFilter requestCounterFilter) {
        List<SecurityFilterChain> filterChains = new ArrayList<>();

        filterChains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/**"), userFilter));
        filterChains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/**"), requestCounterFilter));


        return new FilterChainProxy(filterChains);
    }
}
