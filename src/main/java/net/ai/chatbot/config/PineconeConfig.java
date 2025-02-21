package net.ai.chatbot.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//https://docs.spring.io/spring-ai/reference/api/vectordbs/pinecone.html
@Configuration
public class PineconeConfig {

    @Value("${spring.ai.vectorstore.pinecone.apiKey}")
    private String apiKey;

    @Value("${spring.ai.vectorstore.pinecone.projectId}")
    private String projectId;

    @Value("${spring.ai.vectorstore.pinecone.environment}")
    private String environment;

    @Value("${spring.ai.vectorstore.pinecone.index-name}")
    private String indexName;

    @Value("${spring.ai.vectorstore.pinecone.namespace.prefix}")
    private String namespace;

    //This is not @Bean. As namespace will be dynamic based on each customer
    public VectorStore pineconeVectorStore(EmbeddingModel embeddingModel, String customerEmail) {
        return PineconeVectorStore.builder(embeddingModel)
                .apiKey(apiKey)
                .projectId(projectId)
                .environment(environment)
                .indexName(indexName)
                .namespace(namespace + "-" + customerEmail)
                .build();
    }

}
