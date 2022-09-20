package dev.denux.dtp.internal.entities;

import dev.denux.dtp.utils.TypesUtil;

public enum TomlDataType {
    STRING,
    NUMBER,
    INFINITE_POSITIVE,
    INFINITE_NEGATIVE,
    NAN,
    BOOLEAN,
    ARRAY,
    DATETIME,
    TIME;

    public static TomlDataType getDataType(String value) {
        if (TypesUtil.isNumber(value)) return NUMBER;
        if (value.equals("inf") || value.equals("+inf")) {
            return INFINITE_POSITIVE;
        }
        if (value.equals("-inf")) {
            return INFINITE_NEGATIVE;
        }
        if (value.equals("nan") || value.equals("-nan") || value.equals("+nan")) {
            return NAN;
        }
        if (value.equals("true") || value.equals("false")) {
            return BOOLEAN;
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            return ARRAY;
        }
        else return STRING;
    }
}
