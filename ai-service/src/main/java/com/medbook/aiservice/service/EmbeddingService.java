package com.medbook.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    /**
     * PDF → text → chunk → embedding → Qdrant
     */
    public void ingestPdf(MultipartFile file, String category) {

        if (file.isEmpty()) {
            throw new RuntimeException("File upload rỗng");
        }

        try {
            // Đọc thành byte[] để tránh lỗi Stream closed
            byte[] pdfBytes = file.getBytes();

            try (PDDocument doc = PDDocument.load(pdfBytes)) {

                PDFTextStripper stripper = new PDFTextStripper();
                String fullText = stripper.getText(doc);

                if (fullText == null || fullText.isBlank()) {
                    throw new RuntimeException("PDF không có text (có thể là file scan)");
                }

                // Chunk theo 500 từ
                List<String> chunks = splitIntoChunks(fullText, 500);

                List<Document> documents = new ArrayList<>();

                for (int i = 0; i < chunks.size(); i++) {
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("source", file.getOriginalFilename());
                    metadata.put("category", category);
                    metadata.put("chunkIndex", i);
                    metadata.put("uploadedAt", Instant.now().toString());

                    documents.add(new Document(chunks.get(i), metadata));
                }

                vectorStore.add(documents);

                log.info("Ingest thành công PDF {} – Tổng {} chunks",
                        file.getOriginalFilename(), chunks.size());

            }

        } catch (Exception e) {
            log.error("Lỗi ingest PDF: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Không thể xử lý file PDF: " + e.getMessage());
        }
    }

    /**
     * Chunk theo số từ (chuẩn RAG)
     */
    private List<String> splitIntoChunks(String text, int maxWords) {
        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        int count = 0;

        for (String w : words) {
            current.append(w).append(" ");
            count++;

            if (count >= maxWords) {
                chunks.add(current.toString().trim());
                current.setLength(0);
                count = 0;
            }
        }

        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }
}
