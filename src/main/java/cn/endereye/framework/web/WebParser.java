package cn.endereye.framework.web;

import java.util.HashMap;

public interface WebParser {
    HashMap<Class<?>, WebParser> parsers = new HashMap<Class<?>, WebParser>() {{
        put(String.class, value -> value);
        put(boolean.class, Boolean::parseBoolean);
        put(Boolean.class, Boolean::parseBoolean);
        put(byte.class, Byte::parseByte);
        put(Byte.class, Byte::parseByte);
        put(short.class, Short::parseShort);
        put(Short.class, Short::parseShort);
        put(int.class, Integer::parseInt);
        put(Integer.class, Integer::parseInt);
        put(long.class, Long::parseLong);
        put(Long.class, Long::parseLong);
        put(float.class, Float::parseFloat);
        put(Float.class, Float::parseFloat);
        put(double.class, Double::parseDouble);
        put(Double.class, Double::parseDouble);
        final WebParser characterParser = value -> {
            if (value.length() != 1)
                throw new IllegalArgumentException();
            return value.charAt(0);
        };
        put(char.class, characterParser);
        put(Character.class, characterParser);
    }};

    Object parse(String value);

    @SuppressWarnings("unchecked")
    static <T> T parse(Class<T> type, String value) {
        return value == null ? null : (T) parsers.get(type).parse(value);
    }
}
