package dev.denux.dtp.util;

import javax.annotation.Nonnull;
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
    @Nonnull
    public static char[] listToCharArray(@Nonnull List<Character> list) {
        char[] chars = new char[list.size()];
        for (int i = 0; i < chars.length; i++) {
            Array.set(chars, i, list.get(i));
        }
        return chars;
    }

    /**
     * Converts a {@link List} of {@link Character} to a {@link String}.
     * @param chars the {@link List} you want to convert.
     * @return the converted {@link String}.
     */
    @Nonnull
    public static String charListToString(@Nonnull List<Character> chars) {
        return new String(listToCharArray(chars));
    }

    /**
     * Adds an (array of) primitives types to and {@link List} of {@link Object}.
     * @param object The {@link Object} that can be an array of primitives or just a normal primitive.
     * @param clazz The {@link java.lang.reflect.Type} of primitive.
     * @param list the {@link List} that will be edited.
     */
    public static void addPrimitiveArrayToList(@Nonnull Object object, @Nonnull Class<?> clazz, @Nonnull List<Object> list) {
        if (clazz.getComponentType() != null) {
            if (!clazz.getComponentType().isPrimitive()) {
                addPrimitiveArrayToList(object, clazz.getComponentType(), list);
            } else {
                list.add(object);
            }
        }
    }
}