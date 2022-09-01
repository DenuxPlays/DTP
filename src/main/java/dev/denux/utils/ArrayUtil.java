package dev.denux.utils;

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
}
