package dev.denux.dtp.utils;

public class PrimitiveUtil {

    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> clazz) {
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
