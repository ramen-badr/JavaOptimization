package org.example;

/**
 * Поле оставлено публичным намеренно для демонстрации bytecode-инструкции putfield в лабораторной работе.
 */
public class ValueObject implements HasValue {
    public int value;

    public ValueObject(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ValueObject{value=" + value + '}';
    }
}
