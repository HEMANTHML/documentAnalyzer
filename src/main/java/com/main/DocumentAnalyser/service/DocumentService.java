package com.main.DocumentAnalyser.service;

import com.main.DocumentAnalyser.dao.response.DocumentResponse;
import com.main.DocumentAnalyser.entity.Document;
import com.main.DocumentAnalyser.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final VectorStore vectorStore;

    @Transactional
    public DocumentResponse ingest(MultipartFile file) throws IOException {

        log.info("Ingest started for file: {}", file.getOriginalFilename());

        // 1. Save metadata with PROCESSING status
        Document doc = Document.builder()
                .originalName(file.getOriginalFilename())
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .status("PROCESSING")
                .build();
        doc = documentRepository.save(doc);
        final UUID docId = doc.getId();
        log.info("Document metadata saved with id: {}", docId);

        try (PDDocument pdf = Loader.loadPDF(file.getBytes())) {

            // 2. Extract text
            int pageCount = pdf.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(pdf);
            log.info("Extracted text length: {} chars from {} pages", fullText.length(), pageCount);

            if (fullText.isBlank()) {
                throw new RuntimeException(
                        "PDF text extraction returned empty — possibly a scanned/image-based PDF"
                );
            }

            // 3. Create Spring AI Document with metadata
            org.springframework.ai.document.Document aiDoc =
                    new org.springframework.ai.document.Document(
                            fullText,
                            Map.of(
                                    "doc_id", docId.toString(),
                                    "filename", file.getOriginalFilename()
                            )
                    );

            // 4. Chunk — 500 tokens, 50 overlap
            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(500)
                    .withMinChunkSizeChars(50)
                    .build();

            List<org.springframework.ai.document.Document> chunks = splitter.apply(List.of(aiDoc));
            log.info("Document {} split into {} chunks", docId, chunks.size());

            // 5. Explicitly copy metadata to every chunk
            //    TokenTextSplitter may not carry metadata forward
            chunks.forEach(chunk -> {
                chunk.getMetadata().put("doc_id", docId.toString());
                chunk.getMetadata().put("filename", file.getOriginalFilename());
            });

            if (!chunks.isEmpty()) {
                log.info("Sample chunk metadata: {}", chunks.get(0).getMetadata());
                log.info("Sample chunk text (first 200 chars): {}",
                        chunks.get(0).getText().substring(0, Math.min(200, chunks.get(0).getText().length())));
            }

            // 6. Embed + store in PGVector
            log.info("Storing {} chunks in PGVector...", chunks.size());
            vectorStore.add(chunks);
            log.info("Chunks stored successfully");

            // 7. Update status to READY
            doc.setPageCount(pageCount);
            doc.setStatus("READY");
            documentRepository.save(doc);
            log.info("Document {} ingestion complete — status: READY", docId);

        } catch (Exception e) {
            doc.setStatus("FAILED");
            documentRepository.save(doc);
            log.error("Ingestion failed for doc {}", docId, e); // full stacktrace
            throw new RuntimeException("Ingestion failed: " + e.getMessage(), e);
        }

        return toResponse(doc);
    }

    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(UUID id) {
        documentRepository.deleteById(id);
        log.info("Document metadata deleted for id: {}", id);
    }

    private DocumentResponse toResponse(Document doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .originalName(doc.getOriginalName())
                .fileSize(doc.getFileSize())
                .pageCount(doc.getPageCount())
                .status(doc.getStatus())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }

}