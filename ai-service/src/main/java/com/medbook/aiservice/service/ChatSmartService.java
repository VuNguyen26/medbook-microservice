package com.medbook.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSmartService {

    private final SearchService searchService;
    private final ChatClient chatClient;

    public Map<String, Object> process(String question) {

        // 1️⃣ Search từ Qdrant
        List<Document> docs = searchService.search(question, 5);

        boolean hasContext = docs != null && !docs.isEmpty();

        String finalPrompt;

        if (hasContext) {
            log.info("📚 [RAG] Tìm thấy {} đoạn tài liệu liên quan", docs.size());

            String context = docs.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n\n---\n\n"));

            finalPrompt = String.format("""
                    Bạn là Trợ lý Y Tế của hệ thống MedBook.
                    Hãy trả lời dựa trên đúng nội dung tài liệu bên dưới.
                    Tuyệt đối KHÔNG được bịa, không được suy đoán.

                    Nếu tài liệu không có thông tin, hãy trả lời:
                    "Xin lỗi, tôi không tìm thấy thông tin trong tài liệu nội bộ MedBook."

                    —————————————
                    📚 TÀI LIỆU:
                    %s
                    —————————————

                    ❓ CÂU HỎI:
                    %s

                    💡 TRẢ LỜI:
                    """, context, question);

        } else {
            log.info("💬 [NO RAG] Không tìm thấy tài liệu liên quan → fallback Chat thường.");

            finalPrompt = """
                    Bạn là trợ lý AI hỗ trợ y tế.
                    Tư vấn thân thiện, dễ hiểu, chính xác.
                    Không nói những điều nguy hiểm.
                    Không tự ý chẩn đoán bệnh.
                    Câu hỏi:
                    """ + question;
        }

        // 2️⃣ Gọi LLM
        String answer = chatClient
                .prompt()
                .user(finalPrompt)
                .call()
                .content();

        // 3️⃣ Trả metadata
        Map<String, Object> res = new HashMap<>();
        res.put("answer", answer);
        res.put("mode", hasContext ? "RAG" : "NORMAL");

        if (hasContext) {
            List<Map<String, Object>> sources = docs.stream().map(doc -> {
                Map<String, Object> map = new HashMap<>();
                map.put("text", doc.getText());
                map.put("metadata", doc.getMetadata());
                return map;
            }).toList();

            res.put("sources", sources);
        }

        return res;
    }
}
