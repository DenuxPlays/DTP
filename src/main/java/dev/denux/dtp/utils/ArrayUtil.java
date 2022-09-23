package dev.denux.dtp.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtil {

    public static char[] listToCharArray(List<Character> list) {
        char[] chars = new char[list.size()];
        for (int i = 0; i < chars.length; i++) {
            Array.set(chars, i, list.get(i));
        }
        return chars;
    }

    public static byte[] listToByteArray(List<String> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++) {
            String str = list.get(i).replace("[", "").replace("]", "");
            Array.set(bytes, i, Byte.parseByte(str));
        }
        return bytes;
    }

    public static Object[] listToObjectArray(List<String> list) {
        Object[] objects = new Object[list.size()];
        for (int i = 0; i < objects.length; i++) {
            Array.set(objects, i, list.get(i));
        }
        return objects;
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

    public static List<String> stringToObjectArray(Object object, Class<?> clazz, List<String> list) {
        String arrayString = object.toString();
        if (list == null) {
            list = new ArrayList<>();
            arrayString = arrayString.substring(1, arrayString.length() - 1);
        }
        if (clazz.getComponentType() != null) {
            if (!clazz.getComponentType().isPrimitive()) {
                return stringToObjectArray(object, clazz.getComponentType(), list);
            }
        }
        arrayString = arrayString.replace("\"", "");
        arrayString = arrayString.replace("'", "");
        list.addAll(new ArrayList<>(Arrays.asList(arrayString.split(","))));
        return list;
    }
}