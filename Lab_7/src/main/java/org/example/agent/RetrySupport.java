package org.example.agent;

import java.util.HashMap;
import java.util.Map;

final class RetrySupport {
    private static final ThreadLocal<Map<String, Integer>> ATTEMPTS =
        ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Long>> STARTS =
        ThreadLocal.withInitial(HashMap::new);

    private RetrySupport() {
    }

    static void onEnter(String methodId, Object[] args) {
        Map<String, Integer> attempts = ATTEMPTS.get();
        int attempt = attempts.getOrDefault(methodId, 0) + 1;
        attempts.put(methodId, attempt);
        STARTS.get().put(methodId, System.nanoTime());
        System.out.println("[Agent] Enter " + methodId + " args=" + java.util.Arrays.toString(args) + " attempt=" + attempt);
    }

    static void onSuccess(String methodId) {
        Long start = STARTS.get().remove(methodId);
        if (start != null) {
            long duration = System.nanoTime() - start;
            System.out.println("[Agent] Exit " + methodId + " time=" + duration + "ns");
        }
        clear(methodId);
    }

    static boolean onFailure(String methodId, Throwable error, int maxRetries) {
        Long start = STARTS.get().get(methodId);
        if (start != null) {
            long duration = System.nanoTime() - start;
            System.out.println("[Agent] Exception in " + methodId + " time=" + duration + "ns: " + error);
        } else {
            System.out.println("[Agent] Exception in " + methodId + ": " + error);
        }
        int attempt = ATTEMPTS.get().getOrDefault(methodId, 1);
        return attempt <= maxRetries;
    }

    static void clear(String methodId) {
        Map<String, Integer> attempts = ATTEMPTS.get();
        attempts.remove(methodId);
        if (attempts.isEmpty()) {
            ATTEMPTS.remove();
        }
        Map<String, Long> starts = STARTS.get();
        starts.remove(methodId);
        if (starts.isEmpty()) {
            STARTS.remove();
        }
    }
}
