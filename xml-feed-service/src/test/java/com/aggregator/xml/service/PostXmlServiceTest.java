package com.aggregator.xml.service;

import com.aggregator.xml.model.dto.PostsXmlResponse;
import com.aggregator.xml.model.entity.Post;
import com.aggregator.xml.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostXmlServiceTest {

    @Mock
    private PostRepository postRepository;

    private PostXmlService postXmlService;

    @BeforeEach
    void setUp() {
        postXmlService = new PostXmlService(postRepository);
    }

    @Test
    void shouldReturnAllPostsAsXmlResponse() {
        Post post1 = new Post(1L, 1L, "Title 1", "Body 1");
        Post post2 = new Post(2L, 2L, "Title 2", "Body 2");

        when(postRepository.findAllByOrderByIdAsc()).thenReturn(List.of(post1, post2));

        PostsXmlResponse response = postXmlService.getAllPostsAsXml();

        assertNotNull(response);
        assertEquals(2, response.rows().size());
        assertEquals("Title 1", response.rows().get(0).title());
        assertEquals(1L, response.rows().get(0).userId());
        assertEquals("Title 2", response.rows().get(1).title());
        assertEquals(2L, response.rows().get(1).userId());
    }

    @Test
    void shouldReturnPostsFilteredByUserIdAsXmlResponse() {
        Post post = new Post(10L, 5L, "User 5 Title", "User 5 Body");

        when(postRepository.findByUserIdOrderByIdAsc(5L)).thenReturn(List.of(post));

        PostsXmlResponse response = postXmlService.getPostsByUserIdAsXml(5L);

        assertNotNull(response);
        assertEquals(1, response.rows().size());
        assertEquals(5L, response.rows().get(0).userId());
        assertEquals(10L, response.rows().get(0).id());
        assertEquals("User 5 Title", response.rows().get(0).title());
    }
}
