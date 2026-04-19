# Lab 6 — сортировка + ProGuard

Сделано на основе предыдущей работы из ветки `lab_5`.

## Что реализовано

1. Перенесена ручная сортировка из `lab_5`:
   - `org.example.ManualValueSorter.sortAscendingByValue(...)`
   - `org.example.Main` с генерацией, выводом до/после сортировки.

2. Добавлены **5 пустых классов**:
   - `EmptyClassOne`, `EmptyClassTwo`, `EmptyClassThree`, `EmptyClassFour`, `EmptyClassFive`.

3. В каждом из этих классов по **5 пустых методов**:
   - `methodOne()`
   - `methodTwo()`
   - `methodThree()`
   - `keepByName()`
   - `keepBySignature(String, int)`

4. Настроены 5 сценариев ProGuard:
   - `proguardNoOp` — «ничего не произошло»
   - `proguardFull` — максимум вырезания и обфускации
   - `proguardKeepClasses` — не трогать некоторые классы по имени
   - `proguardKeepMethodName` — не трогать методы по имени
   - `proguardKeepMethodSignature` — не трогать методы по сигнатуре

Конфиги лежат в `proguard`.

Ссылка на ProGuard: https://www.guardsquare.com/proguard

---

## Как собрать, протестировать и прогнать ProGuard

```bash
cd Lab_6
./gradlew clean test proguardAll
```

Артефакты:
- JAR приложения: `build/libs/Lab_6-1.0-SNAPSHOT.jar`
- JAR после ProGuard: `build/proguard/<scenario>/output.jar`
- Mapping/usage/seeds: `build/proguard/<scenario>/...`

---

## Что получилось в каждом сценарии

Проверка классов в JAR:

```bash
cd Lab_6
for s in no-op full keep-classes keep-method-name keep-method-signature; do
  echo "=== $s ==="
  jar tf build/proguard/$s/output.jar | sort
  echo
 done
```

### 1) `no-op`
В JAR остаются исходные классы (`Main`, `ManualValueSorter`, `ValueObject`, `EmptyClass*` и т.д.).

### 2) `full`
Остаются только необходимые классы:
- `org/example/Main.class`
- `org/example/a.class` (обфусцированный `ValueObject`)

Неиспользуемые классы вырезаны.

### 3) `keep-classes`
Классы `EmptyClassOne` и `EmptyClassTwo` сохранены без переименования, даже если они не используются.

### 4) `keep-method-name`
Имя метода `keepByName()` сохранено, остальные методы обфусцированы.

### 5) `keep-method-signature`
Метод с сигнатурой `void keepBySignature(String,int)` сохранен по имени, остальные методы могут быть переименованы.

---

## Просмотр через `javap`

### Команды

```bash
cd Lab_6

# До/после для конкретных сценариев
javap -classpath build/proguard/no-op/output.jar org.example.Main
javap -classpath build/proguard/keep-method-name/output.jar org.example.c
javap -classpath build/proguard/keep-method-signature/output.jar org.example.c
```

### Пример наблюдения (по факту выполнения)

`keep-method-name`:
- `keepByName()` осталось как есть;
- `keepBySignature(String,int)` переименован в `a(String,int)`.

`keep-method-signature`:
- `keepBySignature(String,int)` осталось как есть;
- `keepByName()` переименован в `d()`.

Это видно в выводе `javap` и подтверждается `mapping.txt`.

---

## Дополнительно (Recaf)

Можно открыть любой `build/proguard/<scenario>/output.jar` в Recaf и сравнить:
- какие классы удалены,
- какие имена классов/методов изменены,
- какие имена сохранены keep-правилами.
