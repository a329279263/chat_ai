package cn.autumn.chat.security;


/**
 * shiro 工具类
 */
public class ShiroUtils2 {

    // 线程变量清理！
    public static void cleanUser() {
        _currentUser.remove();
    }

    static ThreadLocal<String> _currentUser = new ThreadLocal<>();

    public static void setUser(String openid) {
        _currentUser.set(openid);
    }

    public static String getUser() {
        return _currentUser.get();
    }

}
