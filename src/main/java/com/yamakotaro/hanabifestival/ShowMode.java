package com.yamakotaro.hanabifestival;

public enum ShowMode {
    RANDOM,
    SEQUENCE;

    public static ShowMode parse(String value) {
        if (value == null) {
            return RANDOM;
        }
        try {
            return ShowMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return RANDOM;
        }
    }
}
