package cn.autumn.chat.controller;

import cn.autumn.chat.domain.ChatMessageRecord;
import cn.autumn.chat.domain.vo.R;
import cn.autumn.chat.domain.vo.resp.ChatMessageRecordResp;
import cn.autumn.chat.service.ChatMessageRecordService;
import cn.autumn.chat.util.SSEUtils;
import cn.hutool.core.bean.BeanUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @descriptions:
 * @author: LZH
 * @date: 2024/3/17 20:08
 * @version: 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/message")
@Data
@Slf4j
public class ChatMessageRecordController {

    private final ChatMessageRecordService messageRecordService;

    /**
     * 根据会话获取最近10条消息
     */
    @GetMapping("/listByChatId")
    public R<List<ChatMessageRecordResp>> listByChatId(@RequestParam("chatId") Long chatId) {
        final List<ChatMessageRecord> list = messageRecordService.listByChatId(chatId);
        return R.ok(BeanUtil.copyToList(list, ChatMessageRecordResp.class));
    }

    /**
     * 删除某个群聊的记录
     */
    @GetMapping("/clearByChatId/{id}")
    public R<?> clearByChatId(@PathVariable Long id) {
        messageRecordService.clearByChatId(id);
        final String groupId = String.valueOf(id);
        SSEUtils.pubMsgToGroup(groupId, "", groupId, groupId + " - clear message " );
        return R.ok("ok");
    }
    @RequestMapping(value = "/subscribeNotice", method = RequestMethod.GET, produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter subscribeNotice(HttpServletRequest request) {
        final String groupId = request.getParameter("groupId");
        final String subId = request.getParameter("subId");
        return SSEUtils.addSub(groupId,subId);
    }

}
