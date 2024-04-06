package cn.autumn.chat.domain;

import cn.autumn.chat.constant.Constant;
import cn.hutool.core.util.StrUtil;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import io.ebean.DB;
import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = Constant.DB_PREFIX + "chat_message_record")
@DbComment("消息记录表")
public class ChatMessageRecord extends BaseEntity {


    @ManyToOne
    @DbComment("会话列表")
    private Chat chat;

    /**
     * enum ChatMessageRole
     */
    @DbComment("角色")
    @Column(length = 20)
    private String role;

    @OneToOne
    @DbComment("ai回复的消息id")
    private ChatMessageRecord answer;


    @DbComment("内容")
    @Lob
    private String content;


    public ChatMessageRecord(Long chatId, ChatMessageRole role, String message) {
        this.chat = DB.reference(Chat.class, chatId);
        this.role = role.value();
        this.content = message;
    }
    public ChatMessageRecord(Long chatId, ChatMessageRole role, ChatMessageRecord r) {
        this.chat = DB.reference(Chat.class, chatId);
        this.role = role.value();
        this.answer = r;
    }

    public boolean contentNotEmpty() {
        return StrUtil.isNotBlank(content);
    }

}
