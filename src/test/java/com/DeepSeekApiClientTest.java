package com;

import com.controller.ChatRecordController;
import com.entity.ChatRecord;
import com.service.ChatRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DeepSeekApiClientTest {

    @Autowired
    private ChatRecordController chatRecordController;

    @MockBean
    private ChatRecordService chatRecordService;

    @Test
    public void testStartConversationAndSendMessage() {
        int userId = 1;
        String title = "测试对话标题";
        String senderType = "user";
        String content = "测试消息内容";

        ChatRecord expectedChatRecord = new ChatRecord();
        expectedChatRecord.setUserId(userId);
        expectedChatRecord.setConversationTitle(title);
        expectedChatRecord.setSenderType(senderType);
        expectedChatRecord.setMessageContent(content);

        Mockito.when(chatRecordService.startConversationAndSaveMessage(userId, title, senderType, content)).thenReturn(expectedChatRecord);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        ChatRecord resultChatRecord = chatRecordController.startConversationAndSendMessage(userId, title, senderType, content, request);

        assertNotNull(resultChatRecord);
        assertEquals(expectedChatRecord.getUserId(), resultChatRecord.getUserId());
        assertEquals(expectedChatRecord.getConversationTitle(), resultChatRecord.getConversationTitle());
        assertEquals(expectedChatRecord.getSenderType(), resultChatRecord.getSenderType());
        assertEquals(expectedChatRecord.getMessageContent(), resultChatRecord.getMessageContent());
    }

    @Test
    public void testGetMessages() {
        Map<String, Object> params = new HashMap<>();
        params.put("title", "测试对话标题");
        params.put("page", 0);
        params.put("size", 10);

        ChatRecord chatRecord1 = new ChatRecord();
        chatRecord1.setUserId(1);
        chatRecord1.setConversationTitle("测试对话标题");
        chatRecord1.setSenderType("user");
        chatRecord1.setMessageContent("消息1");

        ChatRecord chatRecord2 = new ChatRecord();
        chatRecord2.setUserId(1);
        chatRecord2.setConversationTitle("测试对话标题");
        chatRecord2.setSenderType("bot");
        chatRecord2.setMessageContent("消息2");

        List<ChatRecord> expectedList = Arrays.asList(chatRecord1, chatRecord2);

        Mockito.when(chatRecordService.getMessagesByConversationTitle(params)).thenReturn(expectedList);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/chat/messages");

        List<ChatRecord> resultList = chatRecordController.getMessages(params, request);

        assertNotNull(resultList);
        assertEquals(expectedList.size(), resultList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getUserId(), resultList.get(i).getUserId());
            assertEquals(expectedList.get(i).getConversationTitle(), resultList.get(i).getConversationTitle());
            assertEquals(expectedList.get(i).getSenderType(), resultList.get(i).getSenderType());
            assertEquals(expectedList.get(i).getMessageContent(), resultList.get(i).getMessageContent());
        }
    }
}