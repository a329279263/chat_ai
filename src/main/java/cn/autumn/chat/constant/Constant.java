package cn.autumn.chat.constant;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/12 11:28
 * @version: 1.0
 */
public class Constant {
    public static final String DB_PREFIX = "ai_";

    /**
     * token 过期时间
     */
    public static final int TOKEN_EXPR_TIME = 1000 * 60 * 24000;

    public static final String TOKEN_HEAD = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";


    /**
     * 计数器相关
     */
    public static final String INCREMENT_USER_COUNT_PREFIX = "user_request_counter:";
    public static final String INCREMENT_IP_COUNT_PREFIX = "ip_request_counter:";
    public static final String USER_ID_SORT_COUNTER = "user_id_sort_counter:";
    public static final int MAX_USER_REQUEST_COUNT = 1000;
    public static final int MAX_IP_REQUEST_COUNT = 3000;


    /**
     * 用户初始化问答次数
     */
    public static final int INIT_QA_COUNT = 100;


    /**
     * ai 模型说明
     * 语言模型 GLM-4 5
     * 文生图模型 GLM-4V 5
     * 语言模型 GLM-3-Turbo 5
     * 文生图模型 CogView-3 5
     * 语言模型 Embedding-2 5
     * 语言模型 CharGLM-3 5
     */


    public final static String[] PUBLIC_REQUEST_MATCHERS = {
            "/api/v1/test/**",
            "/wx/user/login",
            "/chat_ai/miniapp/ws/**",// WebSocket 鉴权请查看 WebSocketInterceptor
            "/chat_ai/pc/ws/**",
            "/sse/**",
    };


}
