package com.main.DocumentAnalyser.repository;

import com.main.DocumentAnalyser.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @file: DocumentRepository.java
 * @description:
 * @author: Hemanth Oduri
 * @created: 15-06-2026
 * * Copyright (c) 2026. All rights reserved.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    // Check if a document with the same name already exists
    boolean existsByOriginalName(String originalName);
}
