package com.aggregator.json.service;

import com.aggregator.json.client.TodoClient;
import com.aggregator.json.model.dto.*;
import com.aggregator.json.model.entity.Post;
import com.aggregator.json.repository.PostRepository;
import com.aggregator.json.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregatorServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TodoClient todoClient;

    private AggregatorService aggregatorService;

    @BeforeEach
    void setUp() {
        aggregatorService = new AggregatorService(userRepository, postRepository, todoClient);
    }

    @Test
    void shouldAggregateUserDataWithPostsAndTodos() {
        UserDto dummyUser = new UserDto(
                1L, "Leanne Graham", "bret", "leanne@test.com",
                new AddressDto("Main St", "Apt 1", "City", "12345", new GeoDto("0", "0")),
                "123456789", "example.com",
                new CompanyDto("Corp", "Catch", "bs")
        );

        Post dummyPost = new Post(101L, 1L, "Test Title", "Test Body");
        ExternalTodoDto dummyTodo = new ExternalTodoDto(1L, 201L, "Test Todo", false);

        when(userRepository.findAll()).thenReturn(List.of(dummyUser));
        when(postRepository.findAllByOrderByUserIdAscIdAsc()).thenReturn(List.of(dummyPost));
        when(todoClient.fetchAllTodos()).thenReturn(List.of(dummyTodo));

        List<AggregatedUserResponse> result = aggregatorService.getAggregatedData();

        assertNotNull(result);
        assertEquals(1, result.size());

        AggregatedUserResponse userResponse = result.get(0);
        assertEquals(1L, userResponse.id());
        assertEquals("Leanne Graham", userResponse.name());

        assertEquals(1, userResponse.posts().size());
        assertEquals(101L, userResponse.posts().get(0).id());
        assertEquals("Test Title", userResponse.posts().get(0).title());

        assertEquals(1, userResponse.todos().size());
        assertEquals(201L, userResponse.todos().get(0).id());
        assertEquals("Test Todo", userResponse.todos().get(0).title());
        assertFalse(userResponse.todos().get(0).completed());
    }

    @Test
    void shouldReturnSingleAggregatedUserById() {
        UserDto dummyUser = new UserDto(
                1L, "Leanne Graham", "bret", "leanne@test.com",
                new AddressDto("Main St", "Apt 1", "City", "12345", new GeoDto("0", "0")),
                "123456789", "example.com",
                new CompanyDto("Corp", "Catch", "bs")
        );

        Post dummyPost = new Post(101L, 1L, "Test Title", "Test Body");
        ExternalTodoDto dummyTodo = new ExternalTodoDto(1L, 201L, "Test Todo", true);

        when(userRepository.findById(1L)).thenReturn(dummyUser);
        when(postRepository.findByUserIdOrderByIdAsc(1L)).thenReturn(List.of(dummyPost));
        when(todoClient.fetchAllTodos()).thenReturn(List.of(dummyTodo));

        AggregatedUserResponse response = aggregatorService.getAggregatedDataByUserId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1, response.posts().size());
        assertEquals(1, response.todos().size());
        assertTrue(response.todos().get(0).completed());
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(null);

        AggregatedUserResponse response = aggregatorService.getAggregatedDataByUserId(999L);

        assertNull(response);
        verifyNoInteractions(postRepository);
    }
}
