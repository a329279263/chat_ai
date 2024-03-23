package cn.autumn.chat.util;

import cn.autumn.chat.constant.Constant;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2023/10/18 20:12
 * @version: 1.0
 */
public class PathMathUtil {

    public static boolean whiteListMatch(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (var allowedPath : Constant.PUBLIC_REQUEST_MATCHERS) {
            //需要先判断 多个* 的情况，避免替换了一个* 而导致不匹配
            allowedPath = allowedPath.replace("/**", "");
            allowedPath = allowedPath.replace("/*", "");
            if (path.contains(allowedPath)) {
                return true;
            }
        }
        return false;
    }
}
