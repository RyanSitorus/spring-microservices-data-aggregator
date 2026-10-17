package com.aggregator.json.service;

import com.aggregator.json.client.TodoClient;
import com.aggregator.json.model.dto.*;
import com.aggregator.json.model.entity.Post;
import com.aggregator.json.repository.PostRepository;
import com.aggregator.json.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AggregatorService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TodoClient todoClient;

    public AggregatorService(UserRepository userRepository,
                             PostRepository postRepository,
                             TodoClient todoClient) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.todoClient = todoClient;
    }

    public List<AggregatedUserResponse> getAggregatedData() {
        List<UserDto> users = userRepository.findAll();
        List<Post> posts = postRepository.findAllByOrderByUserIdAscIdAsc();
        List<ExternalTodoDto> todos = todoClient.fetchAllTodos();

        Map<Long, List<PostSummaryDto>> postsByUserId = posts.stream()
                .collect(Collectors.groupingBy(
                        Post::getUserId,
                        Collectors.mapping(
                                p -> new PostSummaryDto(p.getId(), p.getTitle(), p.getBody()),
                                Collectors.toList()
                        )
                ));

        Map<Long, List<TodoSummaryDto>> todosByUserId = todos.stream()
                .collect(Collectors.groupingBy(
                        ExternalTodoDto::userId,
                        Collectors.mapping(
                                t -> new TodoSummaryDto(t.id(), t.title(), t.completed()),
                                Collectors.toList()
                        )
                ));

        return users.stream()
                .map(user -> new AggregatedUserResponse(
                        user.id(),
                        user.name(),
                        user.username(),
                        user.email(),
                        user.address(),
                        user.phone(),
                        user.website(),
                        user.company(),
                        todosByUserId.getOrDefault(user.id(), Collections.emptyList()),
                        postsByUserId.getOrDefault(user.id(), Collections.emptyList())
                ))
                .toList();
    }

    public AggregatedUserResponse getAggregatedDataByUserId(Long userId) {
        UserDto user = userRepository.findById(userId);
        if (user == null) {
            return null;
        }

        List<PostSummaryDto> userPosts = postRepository.findByUserIdOrderByIdAsc(userId).stream()
                .map(p -> new PostSummaryDto(p.getId(), p.getTitle(), p.getBody()))
                .toList();

        List<TodoSummaryDto> userTodos = todoClient.fetchAllTodos().stream()
                .filter(t -> userId.equals(t.userId()))
                .map(t -> new TodoSummaryDto(t.id(), t.title(), t.completed()))
                .toList();

        return new AggregatedUserResponse(
                user.id(),
                user.name(),
                user.username(),
                user.email(),
                user.address(),
                user.phone(),
                user.website(),
                user.company(),
                userTodos,
                userPosts
        );
    }
}
