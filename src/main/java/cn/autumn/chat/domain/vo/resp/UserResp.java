package cn.autumn.chat.domain.vo.resp;

import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResp extends BaseResp {


    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String nickName;
    /**
     * 头像地址
     */
    private String avatarUrl;

    @DbComment("已使用问答次数")
    private Integer usedQACount;

    @DbComment("剩余问答次数")
    private Integer remainingQACount;

}
