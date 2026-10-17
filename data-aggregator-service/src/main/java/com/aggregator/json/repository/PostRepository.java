package com.aggregator.json.repository;

import com.aggregator.json.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByUserIdAscIdAsc();

    List<Post> findByUserIdOrderByIdAsc(Long userId);
}
