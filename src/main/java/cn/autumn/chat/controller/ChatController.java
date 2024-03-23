package cn.autumn.chat.controller;

import cn.autumn.chat.domain.Chat;
import cn.autumn.chat.domain.vo.R;
import cn.autumn.chat.domain.vo.req.ChatReq;
import cn.autumn.chat.domain.vo.resp.ChatLatestResp;
import cn.autumn.chat.domain.vo.resp.ChatResp;
import cn.autumn.chat.service.ChatService;
import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/20 11:30
 * @version: 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/chat")
@Data
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * 查询当前用户最新会话
     */
    @GetMapping("/getLatestByCurrentUser")
    public R<ChatLatestResp> getLatestByCurrentUser() {
        final Chat latestByCurrentUser = chatService.getLatestByCurrentUser();
        return R.ok(BeanUtil.copyProperties(latestByCurrentUser, ChatLatestResp.class));
    }


    /**
     * 创建新对话
     */
    @GetMapping("/createNewChat")
    public R<?> createNewChat() {
        chatService.createNewChat();
        return R.ok("操作成功");
    }


    /**
     * 更改会话名称
     */
    @PostMapping("/updateChatName")
    public R<?> updateChatName(ChatReq req) {
        chatService.updateChatName(req);
        return R.ok("操作成功");
    }


    /**
     * 根据当前用户查询会话列表-历史
     */
    @GetMapping("/listByChatId")
    public R<List<ChatResp>> getByCurrentUser() {
        final List<Chat> byCurrentUser = chatService.getByCurrentUser();
        return R.ok(BeanUtil.copyToList(byCurrentUser, ChatResp.class));
    }

}
