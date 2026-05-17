package org.example.agent;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import org.example.instrumentation.Instrumented;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class RetryTransformer implements ClassFileTransformer {
    private static final String TARGET_PACKAGE = "org.example.demo";
    private static final int MAX_RETRIES = 3;
    private static final String RETRY_ATTEMPT_VAR = "__agentRetryAttempt";

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
                if (Modifier.isAbstract(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
                    continue;
                }
                if (isAlreadyInstrumented(method)) {
                    continue;
                }

                instrumentMethod(ctClass, method);
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

    private boolean isAlreadyInstrumented(CtMethod method) {
        CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
        if (codeAttribute == null) {
            return false;
        }
        LocalVariableAttribute localVariables = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (localVariables == null) {
            return false;
        }
        for (int i = 0; i < localVariables.tableLength(); i++) {
            if (RETRY_ATTEMPT_VAR.equals(localVariables.variableName(i))) {
                return true;
            }
        }
        return false;
    }

    private void instrumentMethod(CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {
        method.addLocalVariable(RETRY_ATTEMPT_VAR, CtClass.intType);

        String displayName = ctClass.getName() + "." + method.getName();
        String before = "{ " + RetrySupport.class.getName() + ".onEnter(\"" + displayName + "\", $args); }";
        method.insertBefore(before);

        String after = "{ " + RetrySupport.class.getName() + ".onSuccess(\"" + displayName + "\"); }";
        method.insertAfter(after, false);

        String catchBody = buildCatchBody(method, displayName);
        method.addCatch(catchBody, ctClass.getClassPool().get("java.lang.Throwable"));
    }

    private String buildCatchBody(CtMethod method, String displayName) throws NotFoundException {
        CtClass returnType = method.getReturnType();
        String retrySupport = RetrySupport.class.getName();
        StringBuilder body = new StringBuilder();
        body.append("{ if (").append(retrySupport).append(".onFailure(\"").append(displayName)
            .append("\", $e, ").append(MAX_RETRIES).append(")) { ");
        if (CtClass.voidType.equals(returnType)) {
            body.append(method.getName()).append("($$); return; ");
        } else {
            body.append("return ($r) ").append(method.getName()).append("($$); ");
        }
        body.append("} ");
        body.append(retrySupport).append(".clear(\"").append(displayName).append("\"); ");
        body.append("throw $e; }");
        return body.toString();
    }
}
