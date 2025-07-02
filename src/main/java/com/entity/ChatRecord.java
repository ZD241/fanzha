package com.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天记录
 *
 *
 */
@TableName("chat_record")
public class ChatRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public ChatRecord() {
    }

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;

    /**
     * 用户
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 对话标题
     */
    @TableField(value = "conversation_title")
    private String conversationTitle;

    /**
     * 对话开始时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @TableField(value = "conversation_start_time", fill = FieldFill.INSERT)
    private Date conversationStartTime;

    /**
     * 发送者类型
     */
    @TableField(value = "sender_type")
    private String senderType;

    /**
     * 消息内容
     */
    @TableField(value = "message_content")
    private String messageContent;

    /**
     * 发送时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @TableField(value = "send_time", fill = FieldFill.INSERT)
    private Date sendTime;

    /**
     * 设置：主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 获取：主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 设置：用户
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 获取：用户
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 设置：对话标题
     */
    public String getConversationTitle() {
        return conversationTitle;
    }

    /**
     * 获取：对话标题
     */
    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    /**
     * 设置：对话开始时间
     */
    public Date getConversationStartTime() {
        return conversationStartTime;
    }

    /**
     * 获取：对话开始时间
     */
    public void setConversationStartTime(Date conversationStartTime) {
        this.conversationStartTime = conversationStartTime;
    }

    /**
     * 设置：发送者类型
     */
    public String getSenderType() {
        return senderType;
    }

    /**
     * 获取：发送者类型
     */
    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    /**
     * 设置：消息内容
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * 获取：消息内容
     */
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * 设置：发送时间
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * 获取：发送时间
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "ChatRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", conversationTitle='" + conversationTitle + '\'' +
                ", conversationStartTime=" + conversationStartTime +
                ", senderType='" + senderType + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", sendTime=" + sendTime +
                '}';
    }
}