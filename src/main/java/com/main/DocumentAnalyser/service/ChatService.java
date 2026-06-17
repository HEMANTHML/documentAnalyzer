package com.main.DocumentAnalyser.service;

import com.main.DocumentAnalyser.dao.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public ChatResponse ask(UUID docId, String question) {

        log.info("Chat request — docId: {}, question: {}", docId, question);

        // 1. Build filter — only search chunks from this document
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        var filter = b.eq("doc_id", docId.toString()).build();

        // 2. Semantic similarity search — top 4 chunks
        List<Document> chunks = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(4)
                        .similarityThreshold(0.3) // lowered from 0.5 — safer default
                        .filterExpression(filter)
                        .build()
        );

        log.info("Similarity search returned {} chunks for docId: {}", chunks.size(), docId);

        if (chunks.isEmpty()) {
            // Fallback — try without filter to diagnose
            List<Document> allChunks = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(question)
                            .topK(4)
                            .build()
            );
            log.warn("No chunks found with filter. Without filter found: {} chunks", allChunks.size());

            return ChatResponse.builder()
                    .answer("I could not find relevant information in this document.")
                    .sources(List.of())
                    .build();
        }

        // 3. Build context string from retrieved chunks
        String context = chunks.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        // 4. Build prompt
        String prompt = """
                You are a helpful assistant that answers questions about documents.
                Answer the question ONLY using the context provided below.
                If the answer is not in the context, say:
                'I don't have enough information in this document to answer that.'
                Do not make up information.

                Context:
                %s

                Question: %s
                """.formatted(context, question);

        // 5. Call llama3.2 via Ollama
        log.info("Calling Ollama llama3.2 for docId: {}", docId);
        String answer = chatModel.call(prompt);
        log.info("Ollama responded successfully");

        // 6. Return answer + source snippets
        List<String> sources = chunks.stream()
                .map(c -> c.getText().substring(0, Math.min(200, c.getText().length())) + "...")
                .collect(Collectors.toList());

        return ChatResponse.builder()
                .answer(answer)
                .sources(sources)
                .build();
    }
}