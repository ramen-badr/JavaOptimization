package org.example.demo;

import org.example.instrumentation.Instrumented;

import java.util.Random;

public class DemoService {
    private final Random random = new Random();

    @Instrumented
    public String unstableOperation(String value) {
        if (random.nextInt(3) != 0) {
            throw new IllegalStateException("Random failure for " + value);
        }
        return "OK: " + value;
    }

    @Instrumented
    public void unstableVoid(int value) {
        if (value % 2 != 0) {
            throw new IllegalArgumentException("Odd value: " + value);
        }
        System.out.println("Result of unstable void: " + "OK: " + value);
    }

    // Not instrumented to show baseline behavior without retries/logging.
    public String stableOperation(String value) {
        return "OK: " + value;
    }
}
