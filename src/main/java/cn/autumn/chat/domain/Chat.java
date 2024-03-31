package cn.autumn.chat.domain;

import cn.autumn.chat.constant.Constant;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import io.ebean.annotation.DbComment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = Constant.DB_PREFIX + "chat")
@DbComment("会话列表")
@NoArgsConstructor
public class Chat extends BaseEntity {

    @DbComment("列表名称")
    @Column(length = 100)
    private String name;

    @OneToMany(mappedBy = "chat")
    private List<ChatMessageRecord> messageRecordList;


    public Chat(String name) {
        this.name = name.substring(0, Math.min(name.length(), 100));
    }

    public Chat(Long id) {
        this.id=id;
    }

    public List<ChatMessage> getHistoryMessage(int recentCount) {
        messageRecordList.sort(Comparator.comparing(BaseEntity::getId));
        List<ChatMessage> messageList = new ArrayList<>();

        int startIndex = Math.max(0, (messageRecordList.size() - 1)-recentCount);
        int endIndex = messageRecordList.size();

        for (int i = startIndex; i < endIndex; i++) {
            ChatMessageRecord record = messageRecordList.get(i);
            messageList.add(new ChatMessage(record.getRole(), record.getContent()));
        }

        return messageList;
    }
}
