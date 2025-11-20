package com.medbook.aiservice.controller;

import com.medbook.aiservice.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@Slf4j
public class UploadController {

    private final EmbeddingService embeddingService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadPdf(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category
    ) {
        log.info("📄 Nhận request upload PDF, file={}, size={} bytes, category={}",
                file.getOriginalFilename(),
                file.getSize(),
                category
        );

        if (file.isEmpty()) {
            log.error("❌ File rỗng, không thể xử lý");
            return ResponseEntity.badRequest().body("File rỗng, vui lòng chọn file PDF hợp lệ.");
        }

        try {
            embeddingService.ingestPdf(file, category);
            return ResponseEntity.ok("Ingest thành công tài liệu vào Qdrant!");
        } catch (Exception e) {
            log.error("❌ Lỗi xử lý file PDF", e);
            return ResponseEntity.badRequest().body("Lỗi xử lý file PDF: " + e.getMessage());
        }
    }
}
