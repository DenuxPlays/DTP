package dev.denux.dtp.utils;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtil {

    public static char[] listToCharArray(List<Character> list) {
        char[] chars = new char[list.size()];
        for (int i = 0; i < chars.length; i++) {
            Array.set(chars, i, list.get(i));
        }
        return chars;
    }

    public static void addPrimitiveArrayToList(Object object, Class<?> clazz, List<Object> list) {
        if (clazz.getComponentType() != null) {
            if (!clazz.getComponentType().isPrimitive()) {
                addPrimitiveArrayToList(object, clazz.getComponentType(), list);
            } else {
                list.add(object);
            }
        }
    }
}