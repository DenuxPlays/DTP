package dev.denux.dtp.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * A utility that contains methods regarding Arrays.
 */
public class ArrayUtil {

    private ArrayUtil() {
    }

    /**
     * Converts a {@link List} of {@link Character} to a char array.
     *
     * @param list the {@link List} you want to convert.
     * @return the converted char array.
     */
    public static char[] listToCharArray(List<Character> list) {
        char[] chars = new char[list.size()];
        for (int i = 0; i < chars.length; i++) {
            Array.set(chars, i, list.get(i));
        }
        return chars;
    }

    /**
     * Adds an (array of) primitives types to and {@link List} of {@link Object}.
     * @param object The {@link Object} that can be an array of primitives or just a normal primitive.
     * @param clazz The {@link java.lang.reflect.Type} of primitive.
     * @param list the {@link List} that will be edited.
     */
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