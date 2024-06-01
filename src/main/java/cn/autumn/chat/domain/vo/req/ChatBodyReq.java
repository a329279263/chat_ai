package cn.autumn.chat.domain.vo.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/20 11:25
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatBodyReq extends ChatReq{
    private String subId;
    private Long id;
    private String name;
}
