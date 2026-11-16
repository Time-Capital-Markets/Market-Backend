package com.fourmint.platform;
import java.math.BigDecimal;
import java.util.*;
public final class Checks {
    private Checks() {}
    public static void id(String id) {
        required(id, "id");
        if (id.length() > 200 || id.chars().anyMatch(Character::isISOControl)) throw new IllegalArgumentException("id must have at most 200 characters and no control characters");
    }
    public static void required(Object value, String field) {
        if (value == null || value instanceof String s && s.isBlank()) throw new IllegalArgumentException(field + " is required");
    }
    public static void currency(String value, String field) {
        if (value == null || !value.matches("[A-Z]{3}")) throw new IllegalArgumentException(field + " must be a three-letter uppercase currency");
    }
    public static void oneOf(String value, String field, String... choices) {
        if (value == null || !Arrays.asList(choices).contains(value)) throw new IllegalArgumentException(field + " must be one of " + String.join(", ", choices));
    }
    public static void nonNegative(BigDecimal value, String field) {
        required(value, field);
        if (value.signum() < 0) throw new IllegalArgumentException(field + " cannot be negative");
    }
    public static <T> List<T> list(List<T> values) { return values == null ? List.of() : List.copyOf(values); }
    public static <K,V> Map<K,V> map(Map<K,V> values) { return values == null ? Map.of() : Map.copyOf(values); }
}
