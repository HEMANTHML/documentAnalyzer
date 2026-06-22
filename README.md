```markdown
# DocMind — RAG Document Q&A System

Ask questions about your PDF documents using a fully local AI stack.
No API keys. No data leaving your machine.

Built with **Spring Boot 3**, **Spring AI**, **Ollama**, **PGVector**, and **React 18**.

---

## How It Works

```
User uploads PDF
↓
PDFBox extracts text
↓
Split into 500-token chunks with overlap
↓
nomic-embed-text converts each chunk → vector
↓
Vectors stored in PostgreSQL PGVector
↓
User asks a question
↓
Question embedded → similarity search in PGVector
↓
Top 4 matching chunks retrieved
↓
Chunks + question sent to llama3.2 via Ollama
↓
Answer returned with source references
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.x + Spring AI 2.0 |
| LLM | Ollama — llama3.2 |
| Embeddings | Ollama — nomic-embed-text |
| Vector DB | PostgreSQL + PGVector |
| PDF Parsing | Apache PDFBox 3.x |
| Frontend | React 18 + Vite + TailwindCSS |
| DB Migrations | Liquibase |
| Containers | Docker Compose |

---

## Prerequisites

- Java 17+
- Node.js 18+
- Docker Desktop
- Ollama → [https://ollama.com/download](https://ollama.com/download)

---

## Getting Started

### 1. Clone the repo
```bash
git clone https://github.com/HEMANTHML/documentAnalyzer.git
cd documentAnalyzer
```

### 2. Start PostgreSQL with PGVector
```bash
docker compose up -d
```

### 3. Pull Ollama models
```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

### 4. Configure application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/document_analyser_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3.2
spring.ai.ollama.embedding.model=nomic-embed-text
```

### 5. Run the backend
```bash
./mvnw spring-boot:run
```

### 6. Run the frontend
```bash
cd documentanalyser-ui
npm install
npm run dev
```

### 7. Open the app
```
http://localhost:5173
```

---

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/documents/upload` | Upload a PDF |
| `GET` | `/api/documents` | List all uploaded documents |
| `DELETE` | `/api/documents/{id}` | Delete a document + its vectors |
| `POST` | `/api/documents/{id}/chat` | Ask a question about a document |

---

## Project Structure

```
documentAnalyzer/
├── src/main/java/com/main/DocumentAnalyser/
│   ├── resource/
│   │   └── DocumentResource.java      Upload, list, delete, chat endpoints
│   ├── service/
│   │   ├── DocumentService.java       PDF → chunks → embeddings → PGVector
│   │   └── ChatService.java           Similarity search → prompt → Ollama
│   ├── entity/
│   │   └── Document.java              PDF metadata (id, name, size, status)
│   ├── repository/
│   │   └── DocumentRepository.java
│   ├── dto/
│   │   ├── request/ChatRequest.java
│   │   └── response/
│   │       ├── DocumentResponse.java
│   │       └── ChatResponse.java
│   └── exception/
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── db.changelog/
│   │   └── db.changelog-master.xml   Liquibase schema
│   └── application.properties
├── documentanalyser-ui/               React frontend
└── docker-compose.yml
```

---

## Key Concepts

**RAG (Retrieval Augmented Generation)**
Instead of sending the entire document to the LLM (which would hit context limits), only the most semantically relevant chunks are retrieved via vector similarity search and passed as context. The LLM answers strictly from those chunks — no hallucination from outside the document.

**PGVector**
A PostgreSQL extension that adds a native `vector` column type. Stores 768-dimension embeddings from nomic-embed-text and runs cosine similarity search at query time — all inside the same Postgres instance, no separate vector DB needed.

**Ollama**
Runs LLMs locally on your machine. No internet connection required after the initial model pull. llama3.2 handles chat completions, nomic-embed-text handles embedding generation.

**Spring AI**
Spring's official AI integration framework. Auto-configures `ChatModel`, `EmbeddingModel`, and `VectorStore` beans from `application.properties` — no manual bean wiring needed.

---

## Notes

- Only text-based PDFs are supported (PDFs created digitally, not scanned)
- First run: set `spring.ai.vectorstore.pgvector.initialize-schema=true` to auto-create the vector_store table, then set back to `false`
- Model download sizes: llama3.2 ≈ 2GB, nomic-embed-text ≈ 274MB

---

## License