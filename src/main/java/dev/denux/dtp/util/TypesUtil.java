package dev.denux.dtp.util;

import java.util.ArrayList;
import java.util.List;

public class TypesUtil {
    private TypesUtil() {}

    public static Object convertType(String value) {
        if (isNumber(value)) {
            value = value.replace("_", "");
            return Double.parseDouble(value);
        }
        if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        }
        if (value.equals("inf") || value.equals("+inf")) {
            return Double.POSITIVE_INFINITY;
        }
        if (value.equals("-inf")) {
            return Double.NEGATIVE_INFINITY;
        }
        if (value.equals("nan") || value.equals("-nan") || value.equals("+nan")) {
            return Double.NaN;
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            List<String> list = new ArrayList<>();
            String[] values = value.substring(1, value.length() - 1).split(",");
            for (String obj : values) {
                String listItem = obj.trim();
                list.add(listItem);
            }
            return list;
        }
        return value;
    }

    public static boolean isNumber(String value) {
        try {
            value = value.replace("_", "");
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
