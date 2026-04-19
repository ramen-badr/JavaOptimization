package org.example;

import java.util.List;

public final class ManualValueSorter {
    public static void sortAscendingByValue(List<ValueObject> values) {
        for (int i = 0; i < values.size() - 1; i++) {
            for (int j = 0; j < values.size() - 1 - i; j++) {
                if (values.get(j).value > values.get(j + 1).value) {
                    ValueObject temp = values.get(j);
                    values.set(j, values.get(j + 1));
                    values.set(j + 1, temp);
                }
            }
        }
    }
}
