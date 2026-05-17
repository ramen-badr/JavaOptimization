# Lab 7 — Java Agent + Javassist

Сделано по аналогии с предыдущими работами, но с упором на `java.lang.instrument`.

## Что реализовано

1. Аннотация `@Instrumented` для методов, которые нужно модифицировать.
2. Java-агент с поддержкой:
   - запуска при старте через `premain`;
   - подключения после старта через `agentmain`.
3. `ClassFileTransformer` на Javassist:
   - модифицирует **только** методы с аннотацией;
   - добавляет retry (3 повтора после первой ошибки);
   - логирует вход/выход, аргументы и время выполнения.
4. Демонстрация:
   - `DemoService` — методы с аннотацией;
   - `DemoApplication` — основной запуск;
   - `AgentAttacher` — подключение к уже работающему процессу.

---

## Сборка и тесты

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_7
./gradlew clean test jar
```

JAR: `build/libs/Lab_7-1.0-SNAPSHOT.jar`

---

## Запуск через premain (-javaagent)

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_7
java -javaagent:build/libs/Lab_7-1.0-SNAPSHOT.jar -jar build/libs/Lab_7-1.0-SNAPSHOT.jar
```

В выводе появятся логи `[Agent]` для методов с аннотацией.

---

## Запуск через agentmain (attach)

### 1) Запустить приложение без агента

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_7
java -jar build/libs/Lab_7-1.0-SNAPSHOT.jar wait
```

В выводе будет PID процесса.

### 2) Подключить агент к работающему JVM

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_7
java -cp build/libs/Lab_7-1.0-SNAPSHOT.jar org.example.demo.AgentAttacher <PID> build/libs/Lab_7-1.0-SNAPSHOT.jar
```

После подключения в консоли приложения начнут появляться сообщения `[Agent]`.
