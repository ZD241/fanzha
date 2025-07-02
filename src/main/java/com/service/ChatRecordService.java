package com.service;

import com.entity.ChatRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public interface ChatRecordService {
    /**
     * 开启对话并保存消息
     * @param userId 用户 ID
     * @param title 对话标题
     * @param senderType 发送者类型
     * @param content 消息内容
     * @return 保存后的聊天记录
     */
    ChatRecord startConversationAndSaveMessage(int userId, String title, String senderType, String content);

    /**
     * 根据对话标题分页查询聊天记录
     * @param params 查询参数，包含分页信息和对话标题等
     * @return 分页后的聊天记录列表
     */
    List<ChatRecord> getMessagesByConversationTitle(Map<String, Object> params);
}