package com.medbook.aiservice.controller;

import com.medbook.aiservice.service.ChatSmartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class ChatSmartController {

    private final ChatSmartService chatSmartService;

    @PostMapping("/chat-smart")
    public ResponseEntity<?> chatSmart(@RequestBody Map<String, String> payload) {

        String question = payload.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing field: question"));
        }

        log.info("🤖 [ChatSmart] User question = {}", question);

        return ResponseEntity.ok(chatSmartService.process(question));
    }
}
