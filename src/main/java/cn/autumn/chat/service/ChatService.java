package cn.autumn.chat.service;

import cn.autumn.chat.domain.Chat;
import cn.autumn.chat.domain.ChatMessageRecord;
import cn.autumn.chat.domain.DomainService;
import cn.autumn.chat.domain.vo.req.ChatReq;
import io.ebean.DB;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @descriptions: 会话列表服务
 * @author: LZH
 * @date: 2024/3/20 10:38
 * @version: 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class ChatService extends DomainService<Chat> {

    private final ChatMessageRecordService messageRecordService;

    /**
     * 根据当前用户查询会话列表-历史
     */
    public List<Chat> getByCurrentUser() {
        return this.query().where().orderBy("createTime desc").setMaxRows(10).findList();
    }


    /**
     * 创建新对话
     */
    public Chat createNewChat() {
        Chat chatList = new Chat("新对话");
        chatList.save();
        return chatList;
    }
    /**
     * 创建新对话
     */
    public Chat createNewChat(Long id) {
        Chat chatList = new Chat("新对话");
        chatList.setId(id);
        DB.save(chatList);
        return chatList;
    }


    /**
     * 更改会话名称
     */
    public void updateChatName(ChatReq req) {
        final Chat chatList = this.query().where().eq("id", req.getId()).setMaxRows(1).findOneOrEmpty().orElseThrow(() -> new RuntimeException("会话不存在。"));
        chatList.setName(req.getName());
        chatList.updateWithChangeTime();
    }

    /**
     * 获取最新会话-携带最新10条记录
     */
    public Chat getLatestByCurrentUser() {
        final Optional<Chat> chatOptional = this.query().where().setMaxRows(1).findOneOrEmpty();
        if (chatOptional.isPresent()) {
            final Chat chat = chatOptional.get();
            final List<ChatMessageRecord> recordList = messageRecordService.listByChatId(chat.getId());
            chat.setMessageRecordList(recordList);
            return chat;
        }
        return createNewChat();
    }

    /**
     * 获取某个群聊的最新10条记录
     */
    public Chat getLatestById(Long id) {
        final Optional<Chat> chatOptional = Optional.ofNullable(this.findOne(id));
        if (chatOptional.isPresent()) {
            final Chat chat = chatOptional.get();
            final List<ChatMessageRecord> recordList = messageRecordService.listByChatId(chat.getId());
            chat.setMessageRecordList(recordList);
            return chat;
        }
        return createNewChat(id);
    }
}
