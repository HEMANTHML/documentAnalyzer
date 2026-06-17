package com.main.DocumentAnalyser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @file: Document.java
 * @description:
 * @author: Hemanth Oduri
 * @created: 15-06-2026
 * * Copyright (c) 2026. All rights reserved.
 */
@Builder
@Data
@Entity
@NoArgsConstructor  // <-- Hibernate strictly requires this!
@AllArgsConstructor
public class Document {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Original filename shown in the UI
    @Column(name = "original_name")
    private String originalName;

    // Sanitized filename stored on disk (if you store files locally)
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "page_count")
    private Integer pageCount;

    // PROCESSING → READY → FAILED
    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
}

