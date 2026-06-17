package com.main.DocumentAnalyser.dao.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @file: ChatRequest.java
 * @description:
 * @author: Hemanth Oduri
 * @created: 15-06-2026
 * * Copyright (c) 2026. All rights reserved.
 */
@Data
public class ChatRequest {

    @NotBlank(message = "Question cannot be blank")
    private String question;

    // Which document to search in — from the URL path, not body
    // Kept here in case you want a body-based variant
}
