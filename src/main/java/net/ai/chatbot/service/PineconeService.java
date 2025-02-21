package net.ai.chatbot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PineconeService {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    public PineconeService(@Autowired VectorStore vectorStore, @Autowired EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    /**
     * Stores text embeddings along with metadata in Pinecone.
     */
    public void storeDocument(List<Document> documents) {
        vectorStore.add(documents);
    }

    /**
     * Searches for similar vectors using embeddings and metadata filtering.
     */
    public List<Document> search(String query) {
        return vectorStore.similaritySearch(
                SearchRequest
                        .builder()
                        .query(query)
                        .topK(100)
                        .build()
        );
    }
}
