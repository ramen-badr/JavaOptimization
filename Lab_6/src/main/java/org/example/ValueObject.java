package org.example;

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
