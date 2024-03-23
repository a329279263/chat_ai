package cn.autumn.chat.domain.vo.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatLatestResp extends BaseResp {
    /**
     * 会话名称
     */
    private String name;

    /**
     * 最新10条聊天记录
     */
    private List<ChatMessageRecordResp> messageRecordList;

}
