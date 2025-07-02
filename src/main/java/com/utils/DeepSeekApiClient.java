package com.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class DeepSeekApiClient {

    // 从配置文件中读取DeepSeek API的密钥
    @Value("${deepseek.api.key}")
    private String apiKey;

    // 从配置文件中读取DeepSeek API的URL
    @Value("${deepseek.api.url}")
    private String apiUrl;

    // 用于将对象转换为 JSON 字符串和将 JSON 字符串转换为对象的工具类
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 向DeepSeek API发送请求并获取响应
     *
     * @param prompt 用户输入的提示信息
     * @return DeepSeek API返回的响应内容
     * @throws IOException 如果在请求或读取响应过程中发生I/O异常
     */
    public String sendRequest(String prompt) throws IOException {
        HttpURLConnection connection = null;
        try {
            // 创建URL对象
            URL url = new URL(apiUrl);
            // 打开HTTP连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为POST
            connection.setRequestMethod("POST");
            // 设置Authorization请求头，包含API密钥
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            // 设置Content-Type请求头为JSON格式
            connection.setRequestProperty("Content-Type", "application/json");
            // 设置该连接允许输出数据
            connection.setDoOutput(true);

            // 构建符合DeepSeek API要求的JSON请求体
            RequestBody requestBody = new RequestBody();
            requestBody.setModel("deepseek-reasoner");
            requestBody.setMessages(new Message[]{
                    new Message("system", "You are a helpful assistant"),
                    new Message("user", prompt)
            });
            requestBody.setFrequencyPenalty(0);
            requestBody.setMaxTokens(2048);
            requestBody.setPresencePenalty(0);
            requestBody.setResponseFormat(new ResponseFormat("text"));
            requestBody.setStop(null);
            requestBody.setStream(false);
            requestBody.setStreamOptions(null);
            requestBody.setTemperature(1);
            requestBody.setTopP(1);
            requestBody.setTools(null);
            requestBody.setToolChoice("none");
            requestBody.setLogprobs(false);
            requestBody.setTopLogprobs(null);

            String json = objectMapper.writeValueAsString(requestBody);

            // 将JSON请求体写入输出流
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 读取API返回的响应内容
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } catch (IOException e) {
            // 如果发生错误，可能是服务器返回了错误状态码，这里读取错误流
            try (BufferedReader errorBr = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorBr.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                throw new IOException("Error response from server: " + errorResponse.toString(), e);
            }
        }
    }

    // 请求体对应的Java类
    private static class RequestBody {
        private String model;
        private Message[] messages;
        private int frequencyPenalty;
        private int maxTokens;
        private int presencePenalty;
        private ResponseFormat responseFormat;
        private Object stop;
        private boolean stream;
        private Object streamOptions;
        private double temperature;
        private double topP;
        private Object tools;
        private String toolChoice;
        private boolean logprobs;
        private Object topLogprobs;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Message[] getMessages() {
            return messages;
        }

        public void setMessages(Message[] messages) {
            this.messages = messages;
        }

        public int getFrequencyPenalty() {
            return frequencyPenalty;
        }

        public void setFrequencyPenalty(int frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        public int getPresencePenalty() {
            return presencePenalty;
        }

        public void setPresencePenalty(int presencePenalty) {
            this.presencePenalty = presencePenalty;
        }

        public ResponseFormat getResponseFormat() {
            return responseFormat;
        }

        public void setResponseFormat(ResponseFormat responseFormat) {
            this.responseFormat = responseFormat;
        }

        public Object getStop() {
            return stop;
        }

        public void setStop(Object stop) {
            this.stop = stop;
        }

        public boolean isStream() {
            return stream;
        }

        public void setStream(boolean stream) {
            this.stream = stream;
        }

        public Object getStreamOptions() {
            return streamOptions;
        }

        public void setStreamOptions(Object streamOptions) {
            this.streamOptions = streamOptions;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public double getTopP() {
            return topP;
        }

        public void setTopP(double topP) {
            this.topP = topP;
        }

        public Object getTools() {
            return tools;
        }

        public void setTools(Object tools) {
            this.tools = tools;
        }

        public String getToolChoice() {
            return toolChoice;
        }

        public void setToolChoice(String toolChoice) {
            this.toolChoice = toolChoice;
        }

        public boolean isLogprobs() {
            return logprobs;
        }

        public void setLogprobs(boolean logprobs) {
            this.logprobs = logprobs;
        }

        public Object getTopLogprobs() {
            return topLogprobs;
        }

        public void setTopLogprobs(Object topLogprobs) {
            this.topLogprobs = topLogprobs;
        }
    }

    // 请求体中messages数组元素对应的Java类
    private static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    // 请求体中response_format字段对应的Java类
    private static class ResponseFormat {
        private String type;

        public ResponseFormat(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}