package dev.denux.utils;

import java.util.ArrayList;
import java.util.List;

public class TypesUtil {
    private TypesUtil() {
    }

    public static String convertType(String value) {
        if (isNumber(value)) {
            return String.valueOf(Double.parseDouble(value));
        }
        if (value.equals("true") || value.equals("false")) {
            return String.valueOf(Boolean.parseBoolean(value));
        }
        if (value.equals("inf") || value.equals("+inf")) {
            return String.valueOf(Double.POSITIVE_INFINITY);
        }
        if (value.equals("-inf")) {
            return String.valueOf(Double.NEGATIVE_INFINITY);
        }
        if (value.equals("nan") || value.equals("-nan") || value.equals("+nan")) {
            return String.valueOf(Double.NaN);
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            List<String> list = new ArrayList<>();
            String[] values = value.substring(1, value.length() - 1).split(",");
            for (String obj : values) {
                String listItem = obj.trim();
                list.add(listItem);
            }
            return list.toString();
        }
        return value;
    }

    public static boolean isNumber(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
