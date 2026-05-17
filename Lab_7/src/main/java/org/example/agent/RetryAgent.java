package org.example.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class RetryAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        install(inst, false);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        install(inst, true);
    }

    private static void install(Instrumentation inst, boolean retransform) {
        RetryTransformer transformer = new RetryTransformer();
        inst.addTransformer(transformer, true);
        if (retransform) {
            retransformLoadedClasses(inst, transformer);
        }
    }

    private static void retransformLoadedClasses(Instrumentation inst, RetryTransformer transformer) {
        if (!inst.isRetransformClassesSupported()) {
            System.err.println("[Agent] Retransform is not supported.");
            return;
        }

        for (Class<?> loadedClass : inst.getAllLoadedClasses()) {
            if (!inst.isModifiableClass(loadedClass)) {
                continue;
            }
            if (transformer.shouldNotTransform(loadedClass.getName())) {
                continue;
            }
            try {
                inst.retransformClasses(loadedClass);
            } catch (UnmodifiableClassException e) {
                System.err.println("[Agent] Failed to retransform " + loadedClass.getName() + ": " + e.getMessage());
            }
        }
    }
}
