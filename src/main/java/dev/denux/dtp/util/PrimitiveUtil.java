package dev.denux.dtp.util;

import javax.annotation.Nonnull;

/**
 * A utility class that contains useful methods regarding primitive types.
 */
public class PrimitiveUtil {

    private PrimitiveUtil() {}

    /**
     * Wraps a primitive type to their wrapper class.
     * @param clazz the {@link Class} you want to wrap.
     * @return the wrapper class or the given class if they are no wrapper class for it.
     */
    @SuppressWarnings("unchecked")
    public static <T> @Nonnull Class<T> wrap(@Nonnull Class<T> clazz) {
        if (clazz == int.class) return (Class<T>) Integer.class;
        if (clazz == float.class) return (Class<T>) Float.class;
        if (clazz == byte.class) return (Class<T>) Byte.class;
        if (clazz == double.class) return (Class<T>) Double.class;
        if (clazz == long.class) return (Class<T>) Long.class;
        if (clazz == char.class) return (Class<T>) Character.class;
        if (clazz == boolean.class) return (Class<T>) Boolean.class;
        if (clazz == short.class) return (Class<T>) Short.class;
        if (clazz == void.class) return (Class<T>) Void.class;
        return clazz;
    }
}
