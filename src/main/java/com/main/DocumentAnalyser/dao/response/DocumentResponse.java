package com.main.DocumentAnalyser.dao.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file: DocumentResponse.java
 * @description:
 * @author: Hemanth Oduri
 * @created: 15-06-2026
 * * Copyright (c) 2026. All rights reserved.
 */
@Data
@Builder
public class DocumentResponse {
    private UUID id;
    private String originalName;
    private Long fileSize;
    private Integer pageCount;
    private String status;
    private LocalDateTime uploadedAt;

}