package com.aggregator.xml.service;

import com.aggregator.xml.model.dto.PostXmlDto;
import com.aggregator.xml.model.dto.PostsXmlResponse;
import com.aggregator.xml.model.entity.Post;
import com.aggregator.xml.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostXmlService {

    private final PostRepository postRepository;

    public PostXmlService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostsXmlResponse getAllPostsAsXml() {
        List<Post> posts = postRepository.findAllByOrderByIdAsc();
        List<PostXmlDto> rows = posts.stream()
                .map(p -> new PostXmlDto(p.getUserId(), p.getId(), p.getTitle(), p.getBody()))
                .toList();

        return new PostsXmlResponse(rows);
    }

    public PostsXmlResponse getPostsByUserIdAsXml(Long userId) {
        List<Post> posts = postRepository.findByUserIdOrderByIdAsc(userId);
        List<PostXmlDto> rows = posts.stream()
                .map(p -> new PostXmlDto(p.getUserId(), p.getId(), p.getTitle(), p.getBody()))
                .toList();

        return new PostsXmlResponse(rows);
    }
}
