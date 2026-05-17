# Lab 9 — PMD, SpotBugs, JaCoCo на исходниках Gson

## Источник кода
Взяты исходники библиотеки **Gson** (GitHub, ветка `main`):
https://github.com/google/gson/tree/main

В проект перенесены:
- `src/main/java` + `src/main/resources`
- `src/test/java` + `src/test/resources`

Дополнительно файл `GsonBuildConfig` создан из шаблона и зафиксирован в `src/main/java`.

Лицензия Gson: `Lab_9/LICENSE`.

---

## Как собрать и запустить проверки

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_9
./gradlew clean check
```

`check` включает:
- `test`
- `pmdMain`, `pmdTest`
- `spotbugsMain`, `spotbugsTest`
- `jacocoTestReport`

Отчеты лежат в `build/reports/...`.

Примечание по тестам:
- `com.google.gson.integration.*IT` требуют сборки финального JAR с bnd (в оригинале это `mvn clean verify`), поэтому исключены из Gradle `test`.
- `EnumWithObfuscatedTest` рассчитан на запуск после ProGuard-обфускации, поэтому тоже исключен из `test`.

---

## Интеграция PMD

### Где настроено
`build.gradle`:
- подключен плагин `pmd`;
- задан файл правил `config/pmd/pmd.xml`;
- включены HTML и XML отчеты;
- `ignoreFailures = true`, чтобы build не падал на первых нарушениях (но отчеты строятся).

`config/pmd/pmd.xml` включает категории:
- `bestpractices`
- `codestyle`
- `errorprone`

### Что можно настраивать
- набор правил (`ruleSets`, `ruleSetFiles`)
- исключения файлов/пакетов (`exclude`, `excludeFrom`)
- уровень строгости через правила и приоритеты
- формат отчетов (HTML/XML), вывод в консоль

### Отчеты PMD
Файлы:
- `build/reports/pmd/main.html`
- `build/reports/pmd/test.html`
- `build/reports/pmd/main.xml`
- `build/reports/pmd/test.xml`

Содержание:
- список нарушений по файлам
- правило и его описание
- строка/колонка нарушения
- приоритет и категория

---

## Интеграция SpotBugs

### Где настроено
`build.gradle`:
- плагин `com.github.spotbugs`;
- `effort = 'max'`, `reportLevel = 'low'`;
- HTML и XML отчеты;
- `ignoreFailures = true`.

### Что можно настраивать
- уровень анализа (`effort`)
- уровень отчетности (`reportLevel`)
- исключения (`excludeFilter`)
- подключение дополнительных плагинов SpotBugs

### Отчеты SpotBugs
Файлы:
- `build/reports/spotbugs/main.html`
- `build/reports/spotbugs/test.html`
- `build/reports/spotbugs/main.xml`
- `build/reports/spotbugs/test.xml`

Содержание:
- найденные дефекты с категорией и приоритетом
- описание проблемы и рекомендация
- путь к классу/методу
- номер строки

---

## Интеграция JaCoCo

### Где настроено
`build.gradle`:
- плагин `jacoco`;
- включены HTML/XML отчеты;
- явно указаны классы для покрытия:
  `com/google/gson/**` (включая `internal`), без `module-info.class`.

### Что можно настраивать
- версии инструмента (`toolVersion`)
- включение/исключение классов (`includes`, `excludes`)
- форматы отчетов (HTML/XML/CSV)
- правила покрытия (минимальные проценты для пакетов/классов/методов)

### Отчеты JaCoCo
Файлы:
- `build/reports/jacoco/test/html/index.html`
- `build/reports/jacoco/test/jacocoTestReport.xml`

Содержание:
- суммарное покрытие (instruction/branch/line/method/class)
- детализация по пакетам и классам
- построчное покрытие кода

---

## Итог
В Lab_9 добавлен полноразмерный проект Gson с тестами и интеграцией PMD, SpotBugs и JaCoCo, а также описанием конфигураций и отчетов.
