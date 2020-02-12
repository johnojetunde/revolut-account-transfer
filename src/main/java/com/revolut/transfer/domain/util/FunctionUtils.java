package com.revolut.transfer.domain.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FunctionUtils {
    public static boolean isNullOrEmpty(String st) {
        return (st == null || st.isEmpty());
    }

    public static <T> List<T> getAllAsList(Map<String, T> database) {
        return database.entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
