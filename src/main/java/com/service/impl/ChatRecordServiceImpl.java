package com.service.impl;


import org.springframework.stereotype.Service;
import com.dao.ChatRecordDao;
import com.entity.ChatRecord;
import com.exception.DeepSeekApiException;//引入自定义异常
import com.service.ChatRecordService;
import com.utils.DeepSeekApiClient; // 引入 DeepSeekApiClient 工具类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.boot.SpringApplication;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("chatRecordService")
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private ChatRecordDao chatRecordDao;

    @Autowired
    private DeepSeekApiClient deepSeekApiClient; // 注入 DeepSeekApiClient

    @Override
    public ChatRecord startConversationAndSaveMessage(int userId, String title, String senderType, String content) {
        // 保存用户的消息
        ChatRecord userRecord = new ChatRecord();
        userRecord.setUserId(userId);
        userRecord.setConversationTitle(title);
        userRecord.setSenderType(senderType);
        userRecord.setMessageContent(content);
        userRecord.setConversationStartTime(new Date());
        userRecord.setSendTime(new Date());
        chatRecordDao.insertChatRecord(userRecord);

        try {
            // 向 DeepSeek 模型发送请求获取回复
            String response = deepSeekApiClient.sendRequest(content);

            // 保存 DeepSeek 模型的回复
            ChatRecord botRecord = new ChatRecord();
            botRecord.setUserId(userId);
            botRecord.setConversationTitle(title);
            botRecord.setSenderType("bot");
            botRecord.setMessageContent(response);
            botRecord.setConversationStartTime(new Date());
            botRecord.setSendTime(new Date());
            chatRecordDao.insertChatRecord(botRecord);

            return botRecord;
        } catch (IOException e) {
            // 处理异常，可根据实际情况记录日志或者返回错误信息
            // 抛出自定义异常
            throw new DeepSeekApiException("与 DeepSeek API 通信时发生错误: " + e.getMessage());

        }
    }

    @Override
    public List<ChatRecord> getMessagesByConversationTitle(Map<String, Object> params) {
        int page = (int) params.get("page");
        int limit = (int) params.get("limit");
        int offset = (page - 1) * limit;
        params.put("offset", offset);
        return chatRecordDao.selectMessagesByConversationTitle(params);
    }
}