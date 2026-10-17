package com.aggregator.json.client;

import com.aggregator.json.model.dto.ExternalTodoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
public class TodoClient {

    private static final Logger log = LoggerFactory.getLogger(TodoClient.class);
    private final WebClient webClient;

    public TodoClient(@Value("${external.todos-url:https://jsonplaceholder.typicode.com/todos}") String todosUrl,
                      WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(todosUrl).build();
    }

    public List<ExternalTodoDto> fetchAllTodos() {
        try {
            List<ExternalTodoDto> todos = webClient.get()
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ExternalTodoDto>>() {})
                    .timeout(Duration.ofSeconds(6))
                    .block();

            return todos != null ? todos : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch external todos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
