# Анализаторы качества и покрытие в Lab_9

Этот файл описывает, как в **Lab_9** подключены PMD, SpotBugs и JaCoCo, что можно настраивать и какие данные содержат их отчеты.

## Как это работает в сборке

Интеграция выполнена в `Lab_9/build.gradle` через стандартные Gradle‑плагины:
- `pmd`
- `com.github.spotbugs`
- `jacoco`

Основная команда для запуска:
```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_9
./gradlew clean check
```

`check` запускает:
- `test`
- `pmdMain`, `pmdTest`
- `spotbugsMain`, `spotbugsTest`
- `jacocoTestReport`

Все отчеты сохраняются в `Lab_9/build/reports/...`.

## PMD

### Где настроено
- `build.gradle`: блок `pmd { ... }`
- файл правил: `config/pmd/pmd.xml`
- включены HTML и XML отчеты

### Что можно настраивать
- **Набор правил**: `ruleSets` и `ruleSetFiles` (подключение других наборов или своих правил)
- **Версию PMD**: `toolVersion`
- **Поведение на ошибках**: `ignoreFailures` (падать сборке или нет)
- **Вывод в консоль**: `consoleOutput`
- **Исключения**: `exclude`/`excludeFrom` в задачах PMD

### Отчеты PMD (что внутри)
Файлы:
- `build/reports/pmd/main.html`
- `build/reports/pmd/test.html`
- `build/reports/pmd/main.xml`
- `build/reports/pmd/test.xml`

Содержимое:
- список нарушений по файлам
- правило и его описание
- позиция (строка/колонка)
- приоритет и категория

## SpotBugs

### Где настроено
- `build.gradle`: блок `spotbugs { ... }`
- включены HTML и XML отчеты

### Что можно настраивать
- **Глубина анализа**: `effort` (`min`, `default`, `max`)
- **Порог отчета**: `reportLevel` (`low`, `medium`, `high`)
- **Исключения**: `excludeFilter` (XML‑файл с масками)
- **Доп. плагины**: подключение через зависимости SpotBugs
- **Поведение на ошибках**: `ignoreFailures`

### Отчеты SpotBugs (что внутри)
Файлы:
- `build/reports/spotbugs/main.html`
- `build/reports/spotbugs/test.html`
- `build/reports/spotbugs/main.xml`
- `build/reports/spotbugs/test.xml`

Содержимое:
- найденные дефекты (bug patterns)
- категория и приоритет
- уверенность/серьезность (confidence)
- класс/метод/строка
- текстовое объяснение и рекомендации

## JaCoCo

### Где настроено
- `build.gradle`: блоки `jacoco { ... }` и `jacocoTestReport { ... }`
- отчеты строятся после `test`
- в покрытие включены классы `com/google/gson/**`, исключен `module-info.class`

### Что можно настраивать
- **Версию инструмента**: `toolVersion`
- **Фильтры покрытия**: `includes`/`excludes` и `classDirectories`
- **Форматы отчетов**: HTML / XML / CSV
- **Правила покрытия**: пороги для пакетов/классов/методов (через `jacocoTestCoverageVerification`)

### Отчеты JaCoCo (что внутри)
Файлы:
- `build/reports/jacoco/test/html/index.html`
- `build/reports/jacoco/test/jacocoTestReport.xml`

Содержимое:
- суммарное покрытие по метрикам: instruction / branch / line / method / class
- детализация по пакетам и классам
- построчное покрытие (HTML)

## Итог
В Lab_9 анализаторы и покрытие интегрированы в стандартный Gradle‑pipeline: при запуске `check` автоматически формируются отчеты PMD, SpotBugs и JaCoCo, которые можно детально анализировать и настраивать через `build.gradle` и файлы правил.
