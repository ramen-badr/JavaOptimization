package org.example;

public class BytecodeLabService {
    public int getStringLength(String value) {
        return value.length();
    }

    public int callGetValue(HasValue target) {
        return target.getValue();
    }

    public void changeJavaField(ValueObject target, int newValue) {
        target.value = newValue;
    }
}
