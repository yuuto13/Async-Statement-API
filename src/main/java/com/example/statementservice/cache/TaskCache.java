package com.example.statementservice.cache;

import com.example.statementservice.model.TaskResult;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class TaskCache {
    private final Cache<String, TaskResult> cache;

    public TaskCache(@Value("${cache.expire-hours}") int expireHours) {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(expireHours, TimeUnit.HOURS)
                .build();
    }

    public void put(String taskId, TaskResult result) {
        cache.put(taskId, result);
    }

    public TaskResult get(String taskId) {
        return cache.getIfPresent(taskId);
    }

    public void delete(String taskId) {
        cache.invalidate(taskId);
    }
}