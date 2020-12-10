package ru.nazariene.elasticmonitor.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TypeConverterUtil {

    private static final Map<String, Function<String, ? extends Comparable>> map = new HashMap<>();

    static {
        map.put("int", Integer::parseInt);
        map.put("double", Double::parseDouble);
        map.put("float", Float::parseFloat);
        map.put("long", Long::parseLong);
        map.put("boolean", Boolean::parseBoolean);
        map.put("string", String::valueOf);
    }

    public static <T extends Comparable> T convert(String data, String type) {
        return (T) map.get(type).apply(data);
    }
}
