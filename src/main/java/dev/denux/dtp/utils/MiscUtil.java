package dev.denux.dtp.utils;

public class MiscUtil {

    @SuppressWarnings("unchecked")
    public static <T> Class<T> warpPrimitives(Class<T> clazz) {
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

    @SuppressWarnings("unchecked")
    public static <T> Class<T> unwrapWrapperClasses(Class<T> clazz) {
        if (clazz == Integer.class) return (Class<T>) int.class;
        if (clazz == Float.class) return (Class<T>) float.class;
        if (clazz == Byte.class) return (Class<T>) byte.class;
        if (clazz == Double.class) return (Class<T>) double.class;
        if (clazz == Long.class) return (Class<T>) long.class;
        if (clazz == Character.class) return (Class<T>) char.class;
        if (clazz == Boolean.class) return (Class<T>) boolean.class;
        if (clazz == Short.class) return (Class<T>) short.class;
        if (clazz == Void.class) return (Class<T>) void.class;
        return clazz;
    }
}
