package com.medbook.aiservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // ============================
    // CHAT MODEL
    // ============================
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    // ============================
    // EMBEDDING MODEL
    // ============================
    @Bean
    public EmbeddingModel embeddingModel(OpenAiApi openAiApi) {
        // Model sẽ lấy theo cấu hình trong application.yml
        return new OpenAiEmbeddingModel(openAiApi);
    }
}
