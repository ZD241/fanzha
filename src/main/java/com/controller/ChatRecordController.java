package com.controller;

import com.entity.ChatRecord;
import com.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@Controller
@RequestMapping("/api/chat")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;

    /**
     * 开启对话并保存消息
     * @param userId 用户 ID
     * @param title 对话标题
     * @param senderType 发送者类型
     * @param content 消息内容
     * @param request HttpServletRequest 对象
     * @return 保存后的聊天记录
     */
    @PostMapping("/start-conversation-and-send-message")
    @ResponseBody
    public ChatRecord startConversationAndSendMessage(
            @RequestParam int userId,
            @RequestParam String title,
            @RequestParam String senderType,
            @RequestParam String content,
            HttpServletRequest request
    ) {
        // 获取客户端 IP 地址
        String clientIp = request.getRemoteAddr();
        System.out.println("客户端 IP 地址: " + clientIp);

        return chatRecordService.startConversationAndSaveMessage(userId, title, senderType, content);
    }

    /**
     * 根据对话标题分页查询聊天记录
     * @param params 查询参数，包含分页信息和对话标题等
     * @param request HttpServletRequest 对象
     * @return 分页后的聊天记录列表
     */
    @GetMapping("/messages")
    public List<ChatRecord> getMessages(
            @RequestParam Map<String, Object> params,
            HttpServletRequest request
    ) {
        // 获取请求的 URI
        String requestUri = request.getRequestURI();
        System.out.println("请求的 URI: " + requestUri);
        return chatRecordService.getMessagesByConversationTitle(params);
    }
}