package io.mosip.mimoto.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthSessionRegistry {

    private final Map<String, CompletableFuture<String>> pendingCodes = new ConcurrentHashMap<>();
    private final Map<String, Object> results = new ConcurrentHashMap<>();


    public void createSession(String traceId) {
        pendingCodes.put(traceId, new CompletableFuture<>());
    }

    public CompletableFuture<String> getFuture(String traceId) {
        return pendingCodes.get(traceId);
    }

    public void fulfill(String traceId, String code) {
        CompletableFuture<String> future = pendingCodes.remove(traceId);
        if (future != null) {
            future.complete(code);
        }
    }

    public void saveResult(String traceId, Object result) {
        results.put(traceId, result);
    }

    public Object getResult(String traceId) {
        return results.get(traceId);
    }
}

