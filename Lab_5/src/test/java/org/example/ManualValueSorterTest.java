package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualValueSorterTest {
    @Test
    void shouldSortValuesAscending() {
        List<ValueObject> values = new ArrayList<>();

        values.add(new ValueObject(5));
        values.add(new ValueObject(1));
        values.add(new ValueObject(3));
        values.add(new ValueObject(1));

        ManualValueSorter.sortAscendingByValue(values);

        assertEquals(1, values.get(0).value);
        assertEquals(1, values.get(1).value);
        assertEquals(3, values.get(2).value);
        assertEquals(5, values.get(3).value);
    }
}
