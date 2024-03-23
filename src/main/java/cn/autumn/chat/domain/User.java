package cn.autumn.chat.domain;

import cn.autumn.chat.constant.Constant;
import io.ebean.annotation.DbComment;
import io.ebean.annotation.DbDefault;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = Constant.DB_PREFIX + "user")
@DbComment("用户表")
public class User extends BaseEntity {

    /**
     * 自主生成
     */
    private String username;

    private String nickName;
    /**
     * 性别
     */
    private String gender;
    private String language;
    /**
     * 省市区
     */
    private String city;
    private String province;
    private String country;
    /**
     * 头像地址
     */
    private String avatarUrl;

    @DbComment("已使用问答次数")
    @DbDefault("0")
    private Integer usedQACount;

    @DbComment("剩余问答次数")
    private Integer remainingQACount;

    public void qa() {
        this.usedQACount++;
        this.remainingQACount--;
        this.updateWithChangeTime();
    }
}
