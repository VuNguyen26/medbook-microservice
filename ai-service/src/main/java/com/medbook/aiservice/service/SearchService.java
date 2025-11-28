package com.medbook.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final VectorStore vectorStore;

    /**
     * Cải thiện semantic search để hoạt động tốt với câu hỏi ngắn
     * - score_threshold = 0.30 (thay vì default = 0.80)
     * - limit = topK (fetch nhiều hơn)
     * - log khoảng cách similarity để xem chunk nào match
     */
    public List<Document> search(String query, int topK) {

        if (vectorStore == null) {
            log.error("❌ vectorStore = NULL → Kiểm tra AiConfig.java!");
            return List.of();
        }

        // Tối ưu câu hỏi ngắn (dưới 6 ký tự)
        String improvedQuery = improveQuery(query);

        SearchRequest request = SearchRequest.builder()
                .query(improvedQuery)
                .topK(topK)
                .similarityThreshold(0.30)   // 🔥 Cho phép approximate match
                .build();

        List<Document> results = new ArrayList<>();

        try {
            results = vectorStore.similaritySearch(request);

            // Log chi tiết
            log.info("🔍 Query gốc: {}", query);
            log.info("🔍 Query cải thiện: {}", improvedQuery);
            log.info("🔍 Số chunk tìm được: {}", results.size());

            for (Document d : results) {
                log.info("📌 Chunk → score={}", d.getMetadata().get("__distance__"));
            }

            return results;

        } catch (Exception e) {
            log.error("❌ Lỗi khi similaritySearch: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Nếu câu hỏi quá ngắn → rewrite thành câu RAG-friendly hơn
     */
    private String improveQuery(String query) {
        String q = query.trim();

        if (q.length() <= 3) {
            // Ví dụ: "ho", "sốt", "đau"
            return "Triệu chứng: " + q + ". Khi nào cần khám bệnh?";
        }

        if (q.length() <= 10) {
            // Ví dụ: "bị sốt", "đau đầu"
            return "Tư vấn triệu chứng \"" + q + "\" và hướng dẫn chăm sóc.";
        }

        return q;
    }
}
