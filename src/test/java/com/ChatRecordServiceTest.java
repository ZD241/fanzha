package com;
import com.entity.ChatRecord;
import com.service.ChatRecordService;
import com.utils.DeepSeekApiClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
// ChatRecordService类
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;

import java.io.IOException;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ChatRecordServiceTest {

//    @Autowired
//    private ChatRecordService chatRecordService;
//
//    @MockBean
//    private DeepSeekApiClient deepSeekApiClient;
//
//
//    @Test
//    @Rollback(false)
//    public void testStartConversationAndSaveMessage() throws IOException {
//        int userId = 1;
//        String title = "测试对话";
//        String senderType = "user";
//        String content = "这是一个测试问题";
//        String mockResponse = "这是 DeepSeek 的模拟回复";
//
//        // 模拟 DeepSeekApiClient 的 sendRequest 方法返回模拟的回复
//        Mockito.when(deepSeekApiClient.sendRequest(content)).thenReturn(mockResponse);
//
//        // 调用服务层方法
//        ChatRecord chatRecord = chatRecordService.startConversationAndSaveMessage(userId, title, senderType, content);
//
//        // 验证返回的聊天记录不为空
//        assertNotNull(chatRecord);
//        // 验证 DeepSeekApiClient 的 sendRequest 方法被调用
//        Mockito.verify(deepSeekApiClient, Mockito.times(1)).sendRequest(content);
//    }
}