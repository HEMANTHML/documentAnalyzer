package com.main.DocumentAnalyser.dao.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @file: ChatResponse.java
 * @description:
 * @author: Hemanth Oduri
 * @created: 15-06-2026
 * * Copyright (c) 2026. All rights reserved.
 */
@Data
@Builder
public class ChatResponse {
    private String answer;
    // Source chunks shown to user as references
    private List<String> sources;
}
