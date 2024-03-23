package cn.autumn.chat.domain.vo.resp;

import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatMessageRecordResp extends BaseResp {

    /**
     * enum ChatMessageRole
     */
    @DbComment("角色")
    private String role;

    @DbComment("内容")
    private String content;

}
