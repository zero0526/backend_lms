package webtech.online.course.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ObjectParser {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static <T> T parseRow(Class<T> dtoClass, Object[] row) {
        try {
            T dto = dtoClass.getDeclaredConstructor().newInstance();
            Method[] setters = dtoClass.getMethods();
            int index = 0;

            for (Method m : setters) {
                if (m.getName().startsWith("set") && m.getParameterCount() == 1) {
                    if (index >= row.length) break;
                    Object value = row[index];
                    if (value != null) {
                        value = castValue(value, m.getParameterTypes()[0]);
                    }
                    m.invoke(dto, value);
                    index++;
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Object[] to DTO: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> parseList(Class<T> dtoClass, List<Object[]> rows) {
        List<T> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(parseRow(dtoClass, row));
        }
        return result;
    }

    // ----------------- PARSE JSON -----------------
    public static <T> T parseJson(Class<T> dtoClass, String json) {
        try {
            return mapper.readValue(json, dtoClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to DTO: " + e.getMessage(), e);
        }
    }

    public static <T> List<T> parseJsonList(String json, Class<T> dtoClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, dtoClass));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON list to DTO: " + e.getMessage(), e);
        }
    }

    // ----------------- CASTING -----------------
    private static Object castValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;

        if (value instanceof BigInteger) {
            BigInteger bi = (BigInteger) value;
            if (targetType == Long.class || targetType == long.class) return bi.longValue();
            if (targetType == Integer.class || targetType == int.class) return bi.intValue();
        } else if (value instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) value;
            if (targetType == Double.class || targetType == double.class) return bd.doubleValue();
            if (targetType == Float.class || targetType == float.class) return bd.floatValue();
        } else if (value instanceof Number) {
            Number n = (Number) value;
            if (targetType == Long.class || targetType == long.class) return n.longValue();
            if (targetType == Integer.class || targetType == int.class) return n.intValue();
            if (targetType == Double.class || targetType == double.class) return n.doubleValue();
        } else if (value instanceof Timestamp && targetType == java.time.LocalDateTime.class) {
            return ((Timestamp) value).toLocalDateTime();
        }
        return value;
    }
}
