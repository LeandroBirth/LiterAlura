package com.birth.LiterAlura.service;

import com.birth.LiterAlura.model.Book;
import com.birth.LiterAlura.model.Library;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiFetch {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApiFetch(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void fetchData(String apiUrl, Library library) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode booksNode = rootNode.path("results"); // ajuste se necessário conforme a estrutura JSON

                List<Book> books = new ArrayList<>();
                for (JsonNode bookNode : booksNode) {
                    Book book = objectMapper.treeToValue(bookNode, Book.class);
                    books.add(book);
                }
                library.setBooks(books);
            } else {
                throw new RuntimeException("Erro ao buscar da API: " + response.getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar a resposta da API", e);
        }
    }
}
