package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BytecodeLabServiceTest {
    private final BytecodeLabService service = new BytecodeLabService();

    @Test
    void shouldReturnStringLength() {
        assertEquals(5, service.getStringLength("hello"));
    }

    @Test
    void shouldCallMethodOnObjectAndReturnValue() {
        ValueObject valueObject = new ValueObject(42);
        assertEquals(42, service.callGetValue(valueObject));
    }

    @Test
    void shouldChangeJavaFieldValue() {
        ValueObject valueObject = new ValueObject(10);
        service.changeJavaField(valueObject, 99);

        assertEquals(99, valueObject.value);
    }
}
