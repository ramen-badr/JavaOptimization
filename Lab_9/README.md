# Lab 9 — PMD, SpotBugs, JaCoCo

## База для проверки

В качестве «большой» Java-базы взяты исходники **JSON-java**:
- репозиторий: https://github.com/stleary/JSON-java
- в проекте больше 10 исходных файлов и 1000+ строк кода
- тесты из оригинального репозитория перенесены в `src/test/java`

Лицензия исходников сохранена в `LICENSE.JSON-java`.

---

## Что интегрировано

1. **PMD** — статический анализ по набору правил в `config/pmd/ruleset.xml`.
2. **SpotBugs** — анализ байткода с HTML-отчетом.
3. **JaCoCo** — покрытие тестами. В конфигурации явно указаны классы,
   для которых считается покрытие (`org/json/**`).

---

## Сборка и запуск проверок

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_9

# Полный прогон: тесты + PMD + SpotBugs + JaCoCo
./gradlew clean check
```

Дополнительно можно запускать задачи отдельно:

```bash
./gradlew pmdMain pmdTest
./gradlew spotbugsMain spotbugsTest
./gradlew jacocoTestReport
```

---

## Конфигурация анализаторов

### PMD (`build.gradle` + `config/pmd/ruleset.xml`)
Что можно настраивать:
- **набор правил**: `ruleSets`/`ruleSetFiles` (подключать свои правила);
- **строгость**: `ignoreFailures` (падает ли сборка при нарушениях);
- **форматы отчетов**: XML/HTML;
- **исключения**: `exclude`/`excludeRoots` (папки/файлы).

В `ruleset.xml` показан минимальный набор правил:
- `UnusedImports`, `AvoidPrintStackTrace`, `EmptyCatchBlock`;
- `ExcessiveMethodLength` c порогом 200 строк.

### SpotBugs (`build.gradle`)
Что можно настраивать:
- **уровень анализа**: `effort` (`min`, `default`, `max`);
- **уровень отчетов**: `reportLevel` (`low`, `medium`, `high`);
- **падение сборки**: `ignoreFailures`;
- **форматы отчетов**: HTML/XML;
- **исключения/включения**: `excludeFilter`/`includeFilter` (XML-фильтры).

### JaCoCo (`build.gradle`)
Что можно настраивать:
- **версии инструмента**: `toolVersion`;
- **классы для покрытия**: фильтр `classDirectories` (в работе — `org/json/**`);
- **форматы отчетов**: HTML/XML/CSV;
- **порог покрытия**: задача `jacocoTestCoverageVerification`.

---

## Где смотреть отчеты и что в них находится

### PMD
Пути:
- `build/reports/pmd/main.html`
- `build/reports/pmd/test.html`
- `build/reports/pmd/main.xml`

Содержимое:
- сводка по числу нарушений;
- список правил, которые сработали;
- файлы и строки с нарушениями;
- краткое описание каждого правила.

### SpotBugs
Пути:
- `build/reports/spotbugs/main.html`
- `build/reports/spotbugs/test.html`

Содержимое:
- классификация ошибок по типам (BAD_PRACTICE, CORRECTNESS и т.д.);
- уровень приоритета (High/Medium/Low);
- конкретный класс, метод и строка;
- описание возможной проблемы.

### JaCoCo
Пути:
- `build/reports/jacoco/test/html/index.html`
- `build/reports/jacoco/test/jacocoTestReport.xml`

Содержимое:
- покрытие **instruction / branch / line / method / class**;
- разрез по пакетам и классам;
- подсветка строк в исходниках (HTML) с указанием покрытых/непокрытых веток.

---

## Примечание

PMD и SpotBugs настроены с `ignoreFailures = true`, чтобы можно было
получать отчеты даже при наличии предупреждений. При необходимости
это можно отключить, чтобы сборка падала на найденных проблемах.
