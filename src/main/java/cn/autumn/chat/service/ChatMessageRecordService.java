package cn.autumn.chat.service;

import cn.autumn.chat.domain.BaseEntity;
import cn.autumn.chat.domain.Chat;
import cn.autumn.chat.domain.ChatMessageRecord;
import cn.autumn.chat.domain.UserDomainService;
import cn.autumn.chat.exception.BusinessException;
import cn.autumn.chat.util.db.UserDB;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import io.ebean.DB;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        final List<ChatMessageRecord> createTimeDesc = DB.find(ChatMessageRecord.class).where().eq("chat.id", chatId).orderBy("createTime desc").setMaxRows(10).findList();
        createTimeDesc.sort(Comparator.comparing(BaseEntity::getCreateTime));
        return createTimeDesc;
    }

    public ChatMessageRecord createNewMessage(Long chatId, String message) {
        final ChatMessageRecord userMsg = new ChatMessageRecord(chatId, ChatMessageRole.USER, message);
        this.save(userMsg);
        //保存ai生成的回答
        this.save(new ChatMessageRecord(chatId, ChatMessageRole.ASSISTANT, userMsg));
        return userMsg;
    }

    public void clearByChatId(Long chatId) {
        final Chat chat = UserDB.find(Chat.class, chatId);
        if (chat == null) {
            throw new BusinessException("没有权限！");
        }
        DB.deleteAll(DB.find(ChatMessageRecord.class).where().eq("chat", chat).findList());
    }

    public Optional<ChatMessageRecord> getByAnswerId(Long msgId) {
        return this.query().where().eq("answer.id", msgId).findOneOrEmpty();
    }
}
