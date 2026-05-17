package org.example.agent;

import java.util.HashMap;
import java.util.Map;

final class RetrySupport {
    private static final ThreadLocal<Map<String, Integer>> ATTEMPTS =
        ThreadLocal.withInitial(HashMap::new);

    private RetrySupport() {
    }

    static int nextAttempt(String methodId) {
        Map<String, Integer> attempts = ATTEMPTS.get();
        int attempt = attempts.getOrDefault(methodId, 0) + 1;
        attempts.put(methodId, attempt);
        return attempt;
    }

    static void clear(String methodId) {
        Map<String, Integer> attempts = ATTEMPTS.get();
        attempts.remove(methodId);
        if (attempts.isEmpty()) {
            ATTEMPTS.remove();
        }
    }
}
