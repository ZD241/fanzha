package com.dao;

import com.entity.ChatRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ChatRecordDao {
    /**
     * 插入聊天记录
     * @param record 聊天记录实体
     * @return 插入成功的记录数
     */
    int insertChatRecord(ChatRecord record);

    /**
     * 根据对话标题分页查询聊天记录
     * @param params 查询参数，包含分页信息和对话标题等
     * @return 分页后的聊天记录列表
     */
    List<ChatRecord> selectMessagesByConversationTitle(Map<String, Object> params);
}