package cn.autumn.chat.domain.vo.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatResp extends BaseResp {
    /**
     * 会话名称
     */
    private String name;

}
