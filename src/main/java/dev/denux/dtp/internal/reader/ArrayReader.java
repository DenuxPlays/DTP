package dev.denux.dtp.internal.reader;

import dev.denux.dtp.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class ArrayReader {

    public static List<String> readArray(String value) {
        List<String> list = new ArrayList<>();
        char[] chars = value.toCharArray();
        boolean needEscaping = false;
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            char previousChar = i == 0 ? ' ' : chars[i - 1];
            if (!needEscaping) {
                if (c == '#') {
                    continue;
                }
                if (c == '[') {
                    continue;
                }
                if (c == ']') {
                    if (!val.toString().isEmpty()) {
                        list.add(val.toString().trim());
                        val = new StringBuilder();
                    }
                    continue;
                }
            }
            if (Constant.STRING_INDICATORS.contains(c)) {
                if (previousChar == '\\') {
                    val.append(c);
                    continue;
                }
                if (!needEscaping) {
                    if (!val.toString().isEmpty()) {
                        list.add(val.toString());
                        val = new StringBuilder();
                    } else {
                        needEscaping = true;
                    }
                } else {
                    list.add(val.toString());
                    val = new StringBuilder();
                }
                continue;
            }
            if (c == ',' && !needEscaping && previousChar != '\"') {
                list.add(val.toString().trim());
                val = new StringBuilder();
                continue;
            }
            if (c != ' ') {
                val.append(c);
            }
        }
        return list;
    }

    public static boolean endOfMultilineArray(String line) {
        char[] chars = line.toCharArray();
        boolean string = false;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            char previousChar = i == 0 ? ' ' : chars[i - 1];
            if (Constant.STRING_INDICATORS.contains(c)) {
                if (!string) string = true;
                if (previousChar == '\\') continue;
                string = false;
                continue;
            }
            if (c == ']' && !string) {
                return true;
            }
        }
        return false;
    }
}