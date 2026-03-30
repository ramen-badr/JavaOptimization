package org.example;

public class BytecodeLabService {
    public int getStringLength(String value) {
        return value.length();
    }

    public int callGetValue(HasValue target) {
        return target.getValue();
    }

    /**
     * Метод введен специально для лабораторной демонстрации записи в java-поле (putfield).
     */
    public void changeJavaField(ValueObject target, int newValue) {
        target.value = newValue;
    }
}
