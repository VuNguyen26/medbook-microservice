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

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    /**
     * PDF → cleaned text → smart chunk → embeddings → Qdrant
     */
    public void ingestPdf(MultipartFile file, String category) {

        if (file.isEmpty()) {
            throw new RuntimeException("File upload rỗng");
        }

        try {
            byte[] pdfBytes = file.getBytes();

            String rawText;

            try (PDDocument doc = PDDocument.load(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                rawText = stripper.getText(doc);
            }

            if (rawText == null || rawText.isBlank()) {
                throw new RuntimeException("PDF không có text (có thể là file scan hoặc bị mã hoá)");
            }

            // 1️⃣ Làm sạch text — cực kỳ quan trọng
            String cleaned = cleanText(rawText);

            // 2️⃣ Chunk thông minh theo câu
            List<String> chunks = smartChunk(cleaned, 60); // 60 từ / chunk

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

            log.info("📚 Ingest PDF '{}': tổng {} chunks",
                    file.getOriginalFilename(), chunks.size());

        } catch (Exception e) {
            log.error("❌ Lỗi ingest PDF: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Không thể xử lý file PDF: " + e.getMessage());
        }
    }

    /**
     * Dọn sạch text: remove ký tự ẩn, multiple spaces, header, bullet lỗi
     */
    private String cleanText(String text) {

        return text
                .replaceAll("[\\u0000-\\u001F]", " ")     // Remove control chars
                .replaceAll("•", "- ")                   // Fix bullet
                .replaceAll("[■▪◆●□■◼]", "- ")           // Fix PDF symbols
                .replaceAll("\\s{2,}", " ")              // Multiple spaces
                .replaceAll("\n{2,}", "\n")              // Clean multi newlines
                .trim();
    }

    /**
     * Chunk theo câu để đảm bảo embedding chính xác
     */
    private List<String> smartChunk(String text, int maxWords) {
        List<String> chunks = new ArrayList<>();

        String[] sentences = text.split("(?<=[.!?…])\\s+"); // tách theo dấu câu

        StringBuilder current = new StringBuilder();
        int count = 0;

        for (String s : sentences) {
            int words = s.split("\\s+").length;

            if (count + words > maxWords) {
                chunks.add(current.toString().trim());
                current.setLength(0);
                count = 0;
            }

            current.append(s).append(" ");
            count += words;
        }

        if (current.length() > 0) chunks.add(current.toString().trim());

        return chunks;
    }
}
