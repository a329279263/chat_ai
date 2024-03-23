package cn.autumn.chat.service;

import cn.autumn.chat.domain.ChatMessageRecord;
import cn.autumn.chat.domain.UserDomainService;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import io.ebean.DB;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @descriptions: 群聊消息记录
 * @author: LZH
 * @date: 2024/3/20 10:38
 * @version: 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class ChatMessageRecordService extends UserDomainService<ChatMessageRecord> {

    /**
     * 查询某个会话的聊天记录-10条
     */
    public List<ChatMessageRecord> listByChatId(Long chatId) {
        return DB.find(ChatMessageRecord.class).where().eq("chat.id", chatId).orderBy("createTime").setMaxRows(10).findList();
    }

    public ChatMessageRecord createNewMessage(Long chatId, String message) {
        final ChatMessageRecord chatMessageRecord = new ChatMessageRecord(chatId, ChatMessageRole.USER, message);
        this.save(chatMessageRecord);
        return chatMessageRecord;
    }
}
