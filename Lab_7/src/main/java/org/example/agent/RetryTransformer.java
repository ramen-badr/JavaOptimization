package org.example.agent;

import javassist.*;
import org.example.instrumentation.Instrumented;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class RetryTransformer implements ClassFileTransformer {
    private static final String TARGET_PACKAGE = "org.example.demo";
    private static final String IMPL_SUFFIX = "$impl";
    private static final int MAX_RETRIES = 3;

    boolean shouldNotTransform(String className) {
        if (className == null) {
            return true;
        }
        String normalized = className.replace('/', '.');
        return !normalized.startsWith(TARGET_PACKAGE + ".");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (shouldNotTransform(className)) {
            return null;
        }

        ClassPool pool = new ClassPool(true);
        if (loader != null) {
            pool.appendClassPath(new LoaderClassPath(loader));
        }

        CtClass ctClass = null;
        try {
            ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            boolean modified = false;

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (!isInstrumented(method)) {
                    continue;
                }
                if (method.getName().endsWith(IMPL_SUFFIX)) {
                    continue;
                }
                if (Modifier.isAbstract(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
                    continue;
                }
                if (hasImplMethod(ctClass, method)) {
                    continue;
                }

                String originalName = method.getName();
                String implName = originalName + IMPL_SUFFIX;
                method.setName(implName);
                CtMethod wrapper = CtNewMethod.copy(method, originalName, ctClass, null);
                wrapper.setBody(buildWrapperBody(method, implName, ctClass.getName() + "." + originalName));
                ctClass.addMethod(wrapper);
                modified = true;
            }

            if (!modified) {
                return null;
            }

            return ctClass.toBytecode();
        } catch (Exception e) {
            System.err.println("[Agent] Failed to transform " + className + ": " + e.getMessage());
            return null;
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
    }

    private boolean isInstrumented(CtMethod method) {
        return method.hasAnnotation(Instrumented.class);
    }

    private boolean hasImplMethod(CtClass ctClass, CtMethod method) {
        try {
            ctClass.getDeclaredMethod(method.getName() + IMPL_SUFFIX, method.getParameterTypes());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private String buildWrapperBody(CtMethod method, String implName, String displayName) throws NotFoundException {
        CtClass returnType = method.getReturnType();

        String firstlyLog = "System.out.println(\"[Agent] Enter " + displayName + " args=\" + java.util.Arrays.toString($args) + \" attempt=\" + (__attempt + 1));";
        String successLog = "System.out.println(\"[Agent] Exit " + displayName + " time=\" + __duration + \"ns\");";
        String failureLog = "System.out.println(\"[Agent] Exception in " + displayName + " time=\" + __duration + \"ns: \" + t);";

        StringBuilder body = new StringBuilder();

        body.append("{ int __attempt = 0; while (true) { long __start = System.nanoTime(); ").append(firstlyLog).append(" try { ");

        if (CtClass.voidType.equals(returnType)) {
            body.append(implName).append("($$); ").append("long __duration = System.nanoTime() - __start; ").append(successLog).append(" return; }");
        } else {
            body.append(returnType.getName()).append(" __result = ").append(implName).append("($$); ").append("long __duration = System.nanoTime() - __start; ").append(successLog).append(" return __result; }");
        }

        body.append(" catch (Throwable t) { long __duration = System.nanoTime() - __start; ").append(failureLog).append(" if (__attempt >= ").append(MAX_RETRIES).append(") { throw t; } __attempt++; } }").append(" }");

        return body.toString();
    }
}
