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

        log.info("🤖 [ChatSmart] User question = {}", question);

        String improvedQuestion = improveQuestion(question);

        List<Document> rawDocs = searchService.search(improvedQuestion, 8);

        boolean hasContext = rawDocs != null && !rawDocs.isEmpty();
        String finalPrompt;

        List<Document> docs = rawDocs;

        // ================================
        // CASE 1 — CÓ DỮ LIỆU RAG
        // ================================
        if (hasContext) {

            List<Document> filtered = docs.stream()
                    .filter(d -> {
                        Object dist = d.getMetadata().get("__distance__");
                        if (dist instanceof Number num) {
                            return num.doubleValue() < 0.70;
                        }
                        return true;
                    })
                    .limit(5)
                    .toList();

            if (filtered.isEmpty()) {
                hasContext = false;
            } else {
                docs = filtered;

                log.info(" [RAG] Sử dụng {} đoạn văn sau khi lọc", docs.size());

                String context = docs.stream()
                        .map(Document::getText)
                        .collect(Collectors.joining("\n\n---\n\n"));

                finalPrompt = String.format("""
                        Dưới đây là tài liệu y tế nội bộ của hệ thống MedBook, hãy dùng nó để hỗ trợ người dùng.

                        ===============================
                         TÀI LIỆU NỘI BỘ MEDBOOK:
                        %s
                        ===============================

                         Câu hỏi người dùng:
                        %s
                        """, context, question);

                return buildResponse(finalPrompt, docs, true);
            }
        }

        // ================================
        //  CASE 2 — FALLBACK LLM
        // ================================
        log.info(" [NO RAG] fallback LLM");

        finalPrompt = """
                 Câu hỏi người dùng:
                """ + question;

        return buildResponse(finalPrompt, Collections.emptyList(), false);
    }

    // =========================================================
    // BUILD RESPONSE JSON + SYSTEM PROMPT Y TẾ
    // =========================================================
    private Map<String, Object> buildResponse(String finalPrompt, List<Document> docs, boolean isRag) {

        //  SYSTEM PROMPT — KHÓA MỌI THỨ VỀ PHẠM VI Y TẾ
        String SYSTEM_PROMPT = """
                Bạn là Trợ lý Sức khỏe MedBook — một trợ lý AI chuyên về y tế và kiến thức chăm sóc sức khỏe.

                ⚠ PHẠM VI CHO PHÉP:
                - Chỉ trả lời các câu hỏi liên quan đến sức khỏe, triệu chứng phổ biến, chăm sóc cơ bản, phòng bệnh, cách theo dõi tại nhà.
                - Được phép giải thích nguyên nhân thường gặp, yếu tố nguy cơ, cách xử lý ban đầu an toàn.

                ❌ CẤM TRẢ LỜI:
                - Các chủ đề không liên quan đến y tế: xe cộ, pháp luật, tài chính, công nghệ, xã hội, tình cảm, học tập, giải trí, tôn giáo.
                - Nếu câu hỏi ngoài phạm vi y tế → TRẢ LỜI:
                  "Xin lỗi, tôi chỉ hỗ trợ các vấn đề liên quan đến sức khỏe và y tế trong hệ thống MedBook."

                ❌ CẤM tuyệt đối:
                - Chẩn đoán bệnh cụ thể
                - Kê thuốc
                - Phác đồ điều trị
                - Khẳng định tình trạng nguy hiểm mà không có dấu hiệu rõ ràng

                🎯 PHONG CÁCH TRẢ LỜI:
                - Ngắn gọn, dễ hiểu, thân thiện
                - Không dùng các câu từ chối cứng như:
                  "Tôi không thể chẩn đoán"
                  "Tôi không được phép"
                - Luôn gợi ý an toàn & khi nào nên đi khám

                🔚 KẾT LUẬN BẮT BUỘC:
                "Nếu triệu chứng kéo dài hoặc nặng hơn, bạn nên gặp bác sĩ để được kiểm tra."
                """;

        String answer = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(finalPrompt)
                .call()
                .content();

        Map<String, Object> res = new HashMap<>();
        res.put("answer", answer);
        res.put("mode", isRag ? "RAG" : "NORMAL");

        if (isRag) {
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

    // =========================================================
    //  IMPROVE QUESTION — tối ưu RAG
    // =========================================================
    private String improveQuestion(String q) {
        q = q.trim().toLowerCase();

        if (q.length() <= 3) {
            return "Triệu chứng: " + q + ". Khi nào cần khám bệnh? Dấu hiệu nguy hiểm là gì?";
        }

        if (q.length() <= 10) {
            return "Tư vấn triệu chứng \"" + q + "\" dựa theo tài liệu y tế.";
        }

        return q;
    }
}
