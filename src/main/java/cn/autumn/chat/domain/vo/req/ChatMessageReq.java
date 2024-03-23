package cn.autumn.chat.domain.vo.req;

import lombok.Data;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/12 10:53
 * @version: 1.0
 */
@Data
public class ChatMessageReq {
    private Long listId;

    private String message;
}
