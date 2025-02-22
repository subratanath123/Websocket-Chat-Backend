package net.ai.chatbot.controller;


import net.ai.chatbot.service.ThreadLocalVectorStoreHolder;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/openai")
public class DocumentTrainController {

    private final ThreadLocalVectorStoreHolder threadLocalVectorStoreHolder;

    public DocumentTrainController(ThreadLocalVectorStoreHolder threadLocalVectorStoreHolder) {
        this.threadLocalVectorStoreHolder = threadLocalVectorStoreHolder;
    }

    @PostMapping("/train")
    public ResponseEntity<String> handleFormSubmission(
            @RequestParam(value = "webSite", required = false) String webSite,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {

//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File is required");
//        }

        try {

            if (file != null) {
                byte[] fileBytes = file.getBytes();
                String fileName = file.getOriginalFilename();
                System.out.println("Uploaded file: " + fileName + " (size: " + fileBytes.length + " bytes)");
            }

            System.out.println("Website: " + webSite);
            System.out.println("Description: " + description);

            List<Document> documents = List.of(
                    new Document(description)
            );

            threadLocalVectorStoreHolder.get().add(documents);

            return ResponseEntity.ok("Form submitted successfully!");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing file");
        }
    }

}
