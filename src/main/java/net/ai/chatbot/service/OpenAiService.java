package net.ai.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import net.ai.chatbot.dto.ChatRequest;
import net.ai.chatbot.dto.ChatResponse;
import net.ai.chatbot.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OpenAiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-completions}")
    private int maxCompletions;

    @Value("${openai.temperature}")
    private double temperature;

    @Value("${openai.api.url}")
    private String apiUrl;

    public ChatResponse chat(String prompt) {
        /* Initialize variables */
        ChatResponse chatResponse = null;
        List<Message> ChatMessages = new ArrayList<>();

        ChatRequest.ChatRequestBuilder request = ChatRequest.builder();
        try {
            /* Add user prompt to chat messages */
            ChatMessages.add(new Message("user", prompt));

            /* Build chat request */
            request
                    .model(model)
                    .messages(ChatMessages)
                    .n(maxCompletions)
                    .temperature(temperature);

            /* Send chat request and obtain response */
            chatResponse = restTemplate.postForObject(apiUrl, request.build(), ChatResponse.class);

        } catch (Exception e) {
            log.error("error : " + e.getMessage());
        }

        return chatResponse;
    }

}
