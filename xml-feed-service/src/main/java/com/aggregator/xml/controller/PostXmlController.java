package com.aggregator.xml.controller;

import com.aggregator.xml.model.dto.PostsXmlResponse;
import com.aggregator.xml.service.PostXmlService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostXmlController {

    private final PostXmlService postXmlService;

    public PostXmlController(PostXmlService postXmlService) {
        this.postXmlService = postXmlService;
    }

    @GetMapping(
            value = {"/api/v1/posts/xml", "/posts/xml"},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}
    )
    public ResponseEntity<PostsXmlResponse> getPostsXml(
            @RequestParam(required = false) Long userId
    ) {
        PostsXmlResponse response = (userId != null)
                ? postXmlService.getPostsByUserIdAsXml(userId)
                : postXmlService.getAllPostsAsXml();

        return ResponseEntity.ok(response);
    }
}
