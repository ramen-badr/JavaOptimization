# Lab 5 — Bytecode и ручная сортировка

## Что реализовано

### 1) Класс с требуемыми методами
`org.example.BytecodeLabService`:
- `getStringLength(String value)` — возвращает длину строки.
- `callGetValue(HasValue target)` — вызывает метод объекта и возвращает значение.
- `changeJavaField(ValueObject target, int newValue)` — меняет java-поле объекта.

### 2) Класс с полем `value`
`org.example.ValueObject`:
- публичное поле `int value`;
- `getValue()`;
- `toString()` для удобной печати.

### 3) Ручная сортировка
`org.example.ManualValueSorter.sortAscendingByValue(List<ValueObject>)`:
- пузырьковая сортировка по возрастанию `value`.

### 4) Main
`org.example.Main`:
- создает список из 10 случайных `ValueObject`;
- печатает список до сортировки;
- выполняет ручную сортировку;
- печатает список после сортировки.

---

## Проверка запуска

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_5
./gradlew clean test classes
java -cp build/classes/java/main org.example.Main
```

Пример вывода:

```text
Before sort: [ValueObject{value=44}, ValueObject{value=8}, ...]
After sort: [ValueObject{value=0}, ValueObject{value=8}, ...]
```

---

## `javap -c -v`: как выглядит в байт-коде

Команды:

```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_5
javap -c -v -classpath build/classes/java/main org.example.BytecodeLabService
javap -c -v -classpath build/classes/java/main org.example.ManualValueSorter
javap -c -v -classpath build/classes/java/main org.example.Main
```

### Соответствие Java -> bytecode (ключевые места)

#### BytecodeLabService
- `value.length()` -> `aload_1`, `invokevirtual java/lang/String.length:()I`, `ireturn`
- `target.getValue()` (интерфейс) -> `aload_1`, `invokeinterface org/example/HasValue.getValue:()I`, `ireturn`
- `target.value = newValue` -> `aload_1`, `iload_2`, `putfield org/example/ValueObject.value:I`, `return`

#### ManualValueSorter (пузырек)
- Внешний цикл `for (i...)`:
  - начало: `iconst_0`, `istore_1`
  - проверка условия: `if_icmpge 115` (выход из цикла)
  - инкремент: `iinc 1, 1`
  - переход к началу: `goto 2`
- Внутренний цикл `for (j...)`:
  - начало: `iconst_0`, `istore_2`
  - проверка: `if_icmpge 109`
  - инкремент: `iinc 2, 1`
  - переход: `goto 16`
- `if (values.get(j).value > values.get(j + 1).value)`:
  - сравнение значений,
  - ветвление: `if_icmple 103` (если условие ложное — swap пропускается)
- Swap:
  - чтение `temp` и `List.set(...)` через `invokeinterface`.

#### Main
- Создание объектов -> `new`, `dup`, `invokespecial`.
- Цикл заполнения списка (`i < 10`) -> `if_icmpge 50`, `iinc 3, 1`, `goto 18`.
- Вызов сортировки -> `invokestatic org/example/ManualValueSorter.sortAscendingByValue`.
- Конкатенация строк при печати -> `invokedynamic makeConcatWithConstants`.

---

## Декомпиляция через Recaf

### Как открыть
1. Скачать Recaf: https://github.com/Col-E/Recaf
2. Открыть `Lab_5/build/classes/java/main` или собранный `.jar`.
3. Выбрать классы:
   - `org.example.BytecodeLabService`
   - `org.example.ManualValueSorter`
   - `org.example.Main`

### Что получится в декомпиляции
Декомпилированный код будет близок к исходникам в `src/main/java/org/example`:
- те же методы сервиса;
- те же вложенные циклы и `if` в сортировке;
- тот же сценарий заполнения/сортировки/печати в `main`.

### Изменение через Recaf и проверка
Пример изменения: в `Main.class` изменить строку `"After sort: "` на `"After manual sort: "`, сохранить класс.

Проверка после сохранения:
```bash
cd /home/runner/work/JavaOptimization/JavaOptimization/Lab_5
java -cp build/classes/java/main org.example.Main
```
Ожидаемо во второй строке печати будет новый текст `After manual sort: ...`.

---

## Полезные ссылки
- `javap`: https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javap.html
- JVM Spec (инструкции): https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html
- Recaf: https://github.com/Col-E/Recaf
