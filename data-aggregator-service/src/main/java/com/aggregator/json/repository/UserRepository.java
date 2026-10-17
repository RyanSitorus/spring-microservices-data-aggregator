package com.aggregator.json.repository;

import com.aggregator.json.model.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final String USER_KEY_PREFIX = "user:";
    private static final String USER_IDS_KEY = "user_ids";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public UserRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public List<UserDto> findAll() {
        List<String> userIds = redisTemplate.opsForList().range(USER_IDS_KEY, 0, -1);
        List<UserDto> users = new ArrayList<>();

        if (userIds != null && !userIds.isEmpty()) {
            for (String id : userIds) {
                UserDto user = findById(Long.valueOf(id));
                if (user != null) {
                    users.add(user);
                }
            }
            return users;
        }

        Set<String> keys = redisTemplate.keys(USER_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        for (String key : keys) {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                try {
                    users.add(objectMapper.readValue(json, UserDto.class));
                } catch (JsonProcessingException e) {
                    log.error("Failed to deserialize user from key {}: {}", key, e.getMessage());
                }
            }
        }

        users.sort(Comparator.comparing(UserDto::id));
        return users;
    }

    public UserDto findById(Long id) {
        String json = redisTemplate.opsForValue().get(USER_KEY_PREFIX + id);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, UserDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize user id {}: {}", id, e.getMessage());
            return null;
        }
    }
}
