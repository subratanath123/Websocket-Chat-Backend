package net.ai.chatbot.service;


import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class ThreadLocalVectorStoreHolder {
    private static final ThreadLocal<VectorStore> threadLocalVectorStore = new ThreadLocal<>();

    public void set(VectorStore vectorStore) {
        threadLocalVectorStore.set(vectorStore);
    }

    public VectorStore get() {
        return threadLocalVectorStore.get();
    }

    public void clear() {
        threadLocalVectorStore.remove();
    }
}