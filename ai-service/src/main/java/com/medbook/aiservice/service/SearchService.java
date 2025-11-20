package com.medbook.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final VectorStore vectorStore;

    public List<Document> search(String query, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)      // text để embed
                .topK(topK)        // số đoạn muốn lấy
                .build();

        return vectorStore.similaritySearch(request);
    }
}
