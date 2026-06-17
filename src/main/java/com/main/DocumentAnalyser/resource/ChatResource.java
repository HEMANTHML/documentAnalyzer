package com.main.DocumentAnalyser.resource;

import com.main.DocumentAnalyser.dao.request.ChatRequest;
import com.main.DocumentAnalyser.dao.response.ChatResponse;
import com.main.DocumentAnalyser.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @file: ChatResource.java
 * @description:
 * @author: Hemanth Oduri
 * @created: 15-06-2026
 * * Copyright (c) 2026. All rights reserved.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ChatResource {

    private final ChatService chatService;

    // POST /api/chat/{docId}
    // Body: { "question": "What is this document about?" }
    @PostMapping("/{docId}")
    public ResponseEntity<ChatResponse> ask(
            @PathVariable UUID docId,
            @Valid @RequestBody ChatRequest request) {

        return ResponseEntity.ok(chatService.ask(docId, request.getQuestion()));
    }
}